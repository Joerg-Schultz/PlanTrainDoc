package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.User


/**
 * Test initiation of Room database
 * https://medium.com/androiddevelopers/using-and-testing-room-kotlin-apis-4d69438f9334
 *
 */

@RunWith(AndroidJUnit4::class)
class PTDdbTest{

    /**
     * build the database in memory.
     * Tests only the PTDdb file,not the Database Builder
     */
    @Test
    internal fun getDatabaseTest(){
        val context: Context = ApplicationProvider.getApplicationContext()

        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        val database = Room.inMemoryDatabaseBuilder(context, PTDdb::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        //val database by lazy { PTDdb.getDatabase(context) }
        assertThat(database).isNotNull()
    }
}