package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import com.google.common.truth.Truth.assertThat


/**
 * Test initiation of Room database
 * https://medium.com/androiddevelopers/using-and-testing-room-kotlin-apis-4d69438f9334
 *
 */

@RunWith(AndroidJUnit4::class)
class PTDdbTest{

    @Test
    internal fun getDatabaseTest(){
        val context: Context = ApplicationProvider.getApplicationContext()
        val database by lazy { PTDdb.getDatabase(context) }
        assertThat(database).isNotNull()
    }
}