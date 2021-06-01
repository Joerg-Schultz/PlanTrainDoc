package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.plantraindoc_v1.daos.TrialDao
import de.tierwohlteam.android.plantraindoc_v1.models.Dog
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@SmallTest
@HiltAndroidTest
class PTDRepositoryTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @Test
    @Throws(Exception::class)
    internal fun insertAndGetUserTest() = runBlocking {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de",
            password = "123", role = "standard")
        repository.insertUser(user)
        val dbUser = repository.getUserByID(userID)
        assertThat(dbUser).isEqualTo(user)
    }

    @Test
    internal fun getNonExistingUserByID() = runBlocking{
        val nonUserID = uuid4()
        val dbUser = repository.getUserByID(nonUserID)
        assertThat(dbUser).isNull()
    }

    @Test
    @Throws(Exception::class)
    internal fun insertAndGetDogTest() = runBlocking {
        val userID = uuid4()
        val user = User(id = userID, name = "Test DogUser", email = "testuser@mail.de",
            password = "123", role = "standard")
        repository.insertUser(user)
        val dogID = uuid4()
        val dog = Dog(id = dogID, userID = userID, name = "Luna")
        repository.insertDog(dog)
        val dbDog = repository.getDogByID(dogID)
        assertThat(dbDog).isEqualTo(dog)
    }

    @Test
    internal fun insertAndGetGoal() = runBlocking {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        repository.insertGoal(goal)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //TODO
        //this is called before the tests are finished -> error
        //how can I wait for the co-routines to finish?
        //db.close()
    }
}