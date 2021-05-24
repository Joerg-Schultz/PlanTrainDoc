package de.tierwohlteam.android.plantraindoc_v1.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test initiation of Room database
 * https://medium.com/androiddevelopers/using-and-testing-room-kotlin-apis-4d69438f9334
 *
 */

@RunWith(AndroidJUnit4::class)
class PTDdbTest{
    private lateinit var db: PTDdb

    @Test
    internal fun connectDBTest(){
        assert(true)
    }
}