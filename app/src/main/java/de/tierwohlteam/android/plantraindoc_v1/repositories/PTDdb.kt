package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Build the Room database for PlanTrainDoc
 */
//TODO Add User Entity to fix error
@Database(entities = arrayOf(), version = 1, exportSchema = true)
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
