package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import de.tierwohlteam.android.plantraindoc_v1.daos.*
import kotlinx.datetime.*
import de.tierwohlteam.android.plantraindoc_v1.models.*

/**
 * Build the Room database for PlanTrainDoc
 */
@Database(
    entities = [User::class, Dog::class,
        Goal::class, GoalDependencyCrossRef::class,
        Plan::class, PlanHelper::class, PlanConstraint::class,
        Session::class],
    version = 1, exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PTDdb : RoomDatabase(){

    abstract fun userDao(): UserDao
    abstract fun dogDao(): DogDao
    abstract fun goalDao(): GoalDao
    abstract fun planDao(): PlanDao
    abstract fun sessionDao(): SessionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PTDdb? = null

        /**
         * Return the database object
         * @param[context] the current context of the app
         * @param[test] Boolean, default false. Set to true to generate in memory db for testing
         * @return the database object
         */
        fun getDatabase(context: Context, test: Boolean = false): PTDdb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            // TODO on creation add new user
            return INSTANCE ?: synchronized(this) {
                val instance = if(!test) {
                    Room.databaseBuilder(
                    context.applicationContext,
                    PTDdb::class.java,
                    "plantraindoc_db"
                ).build() } else {
                    Room.inMemoryDatabaseBuilder(context, PTDdb::class.java)
                    // Allowing main thread queries, just for testing.
                    .allowMainThreadQueries()
                    .build()
                }
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun toUUID(uuidString: String?): Uuid? {
        return if(uuidString != null) uuidFrom(uuidString) else null
    }

    @TypeConverter
    fun fromUUID(uuid: Uuid?): String? {
        return if(uuid != null) uuid.toString() else null
    }

    @TypeConverter
    fun toString(localDateTime: LocalDateTime): String {
        return localDateTime.toString()
    }

    @TypeConverter
    fun fromString(localDateTimeString: String): LocalDateTime{
        return localDateTimeString.toLocalDateTime()
    }
}
