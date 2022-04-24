package de.tierwohlteam.android.plantraindoc_v1.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.benasher44.uuid.uuidFrom
import com.google.gson.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.models.ReinforcementScheme
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.others.Constants
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.BASE_URL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USER_ID
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.PTD_DB_NAME
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.BasicAuthInterceptor
import de.tierwohlteam.android.plantraindoc_v1.repositories.remote.PTDapi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
//Philipp used ApplicationComponent, but was renamed
//https://github.com/google/dagger/releases/tag/dagger-2.28.2
object AppModule {

    @Provides
    @Singleton
    fun providePTDdb(
        @ApplicationContext app:Context
    ) = Room.databaseBuilder(
        app.applicationContext,
        PTDdb::class.java,
        PTD_DB_NAME
    ) //.allowMainThreadQueries() //devdebug only!!!!
        //.fallbackToDestructiveMigration() // comment out in production
        .addMigrations(MIGRATION_3_4)
        .build()

    val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE goals ADD COLUMN youtube TEXT")
        }
    }
    @Singleton
    @Provides
    fun provideUserDao(db: PTDdb) = db.userDao()

    @Singleton
    @Provides
    fun provideDogDao(db: PTDdb) = db.dogDao()

    @Singleton
    @Provides
    fun provideGoalDao(db: PTDdb) = db.goalDao()

    @Singleton
    @Provides
    fun providePlanDao(db: PTDdb) = db.planDao()

    @Singleton
    @Provides
    fun provideSessionDao(db: PTDdb) = db.sessionDao()

    @Singleton
    @Provides
    fun provideTrialDao(db: PTDdb) = db.trialDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        //app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)
        PreferenceManager.getDefaultSharedPreferences(app)

    @Singleton
    @Provides
    fun providePTDapi(
        basicAuthInterceptor: BasicAuthInterceptor
    ) : PTDapi {
        val client = OkHttpClient.Builder()
            .addInterceptor(basicAuthInterceptor)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter)
            .create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(PTDapi::class.java)
    }
    //https://medium.com/@haohcraft/deserialize-and-serialize-datetime-with-gson-82ea59e874c7
    //https://sites.google.com/site/gson/gson-user-guide#TOC-Custom-Serialization-and-Deserialization
    object LocalDateTimeAdapter: JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime>{
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDateTime {
            val cleanJson = json.toString().replace("\"","")
            return cleanJson.toLocalDateTime()
        }

        override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
            return JsonPrimitive(src.toString())
        }

    }

    //@Singleton
    @Provides
    fun provideUserID(sharedPreferences: SharedPreferences, repository: PTDRepository): Uuid {
        var userID = sharedPreferences.getString(KEY_USER_ID, null)
        if(userID == null){
            val newUserID = uuid4()
            val user = User(
                id = newUserID, name = Constants.DEFAULT_USER_NAME,
                email = Constants.DEFAULT_USER_EMAIL, password = Constants.DEFAULT_USER_PASSWORD
            )
            val insertJob = GlobalScope.launch {
                repository.insertUser(user)
            }
            userID = newUserID.toString()
            sharedPreferences.edit().putString(KEY_USER_ID, newUserID.toString()).apply()
        } else {
            GlobalScope.launch {
                var userInDB = false
                val dbJob = launch {
                    val dbUsers = repository.getUsers()
                    if (dbUsers.any { it.id.toString() == userID }) userInDB = true
                }
                dbJob.join()
                if (!userInDB) {
                    val newUserID = Uuid.fromString(userID)
                    val user = User(
                        id = newUserID, name = Constants.DEFAULT_USER_NAME,
                        email = Constants.DEFAULT_USER_EMAIL, password = Constants.DEFAULT_USER_PASSWORD
                    )
                    val insertJob = GlobalScope.launch {
                        repository.insertUser(user)
                    }
                    insertJob.join()
                    userID = newUserID.toString()
                    sharedPreferences.edit().putString(KEY_USER_ID, newUserID.toString()).apply()
                }
            }
        }
        return uuidFrom(userID!!)
    }

    @Singleton
    @Provides
    @Named("DurationScheme")
    fun provideDurationReinforcementScheme(@ApplicationContext app: Context) : ReinforcementScheme =
        readSpectorScheme(app, R.raw.durationscheme)
    @Singleton
    @Provides
    @Named("DistanceScheme")
    fun provideDistanceReinforcementScheme(@ApplicationContext app: Context) : ReinforcementScheme =
        readSpectorScheme(app, R.raw.distancescheme)

    private fun readSpectorScheme(context: Context, fileID : Int): ReinforcementScheme {
        val scheme = ReinforcementScheme()
        context.resources?.openRawResource(fileID)?.bufferedReader()?.forEachLine { line ->
            val elements = line.split(":")
            val key = elements.first().toFloat()
            val values = elements[1].split(", ").map { it.toFloat() }
            scheme.addLevel(key, values)
        }
        return scheme
    }

    @Singleton
    @Provides
    fun provideBasicAuthInterceptor() = BasicAuthInterceptor()
}