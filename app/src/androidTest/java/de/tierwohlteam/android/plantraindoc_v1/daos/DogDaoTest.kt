package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.plantraindoc_v1.models.Dog
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class DogDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testDogDao")
    lateinit var dogDao: DogDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetDog() {
        val userID = uuid4()
        val dogID = uuid4()
        val user = User(
            id = userID, name = "Test User", email = "testuser@mail.de",
            password = "123", role = "standard"
        )
        val dog = Dog(id = dogID, userID = userID, name = "Luna")
        repository.insertUser(user)
        dogDao.insert(dog)
        val dbDog = dogDao.getByID(dogID)
        assertThat(dbDog).isEqualTo(dog)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }

}