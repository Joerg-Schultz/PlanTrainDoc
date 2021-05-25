package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import de.tierwohlteam.android.plantraindoc_v1.daos.DogDao
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.daos.UserDao
import de.tierwohlteam.android.plantraindoc_v1.models.Dog

/**
 * Build the Room database for PlanTrainDoc
 */
@Database(entities = [User::class, Dog::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PTDdb : RoomDatabase(){

    abstract fun userDao(): UserDao
    abstract fun dogDao(): DogDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PTDdb? = null

        /**
         * Return the database object
         * @param[context] the current context of the app
         * @return the database object
         */
        fun getDatabase(context: Context): PTDdb {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            // TODO on creation add new user
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PTDdb::class.java,
                    "plantraindoc_db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun toUUID(uuid: String): Uuid {
        return uuidFrom(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: Uuid): String {
        return uuid.toString()
    }
}
