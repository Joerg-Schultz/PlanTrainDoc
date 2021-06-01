package de.tierwohlteam.android.plantraindoc_v1.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import javax.inject.Inject
import javax.inject.Named


/**
 * Test initiation of Room database
 * https://medium.com/androiddevelopers/using-and-testing-room-kotlin-apis-4d69438f9334
 *
 */

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class PTDdbTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: PTDdb

    @Before
    internal fun setup() {
        hiltRule.inject()
    }
    /**
     * build the database in memory.
     * Tests only the PTDdb file,not the Database Builder
     */
    @Test
    internal fun getDatabaseTest(){
        assertThat(db).isNotNull()
    }
}