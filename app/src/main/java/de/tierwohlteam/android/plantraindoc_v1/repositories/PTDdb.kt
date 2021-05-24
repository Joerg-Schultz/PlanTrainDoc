package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.User
import com.benasher44.uuid.uuid4

/**
 * Build the Room database for PlanTrainDoc
 */
@Database(entities = [User::class], version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class PTDdb : RoomDatabase(){

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
    fun toUUID(uuid: String?): Uuid? {
        return toUUID(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: Uuid?): String? {
        return fromUUID(uuid)
    }
}