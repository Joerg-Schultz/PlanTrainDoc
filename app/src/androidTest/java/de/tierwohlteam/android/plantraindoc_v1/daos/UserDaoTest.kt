package de.tierwohlteam.android.plantraindoc_v1.daos

import android.database.sqlite.SQLiteConstraintException
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
import kotlinx.coroutines.runBlocking
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
class UserDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testUserDao")
    lateinit var userDao: UserDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() = runBlocking {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        userDao.insert(user)
        val dbUser = userDao.getByID(userID)
        assertThat(dbUser).isEqualTo(user)
    }
    @Test
    fun notExistingUserByID() = runBlocking {
        val nonUserID = uuid4()
        val dbUser = userDao.getByID(nonUserID)
        assertThat(dbUser).isNull()
    }

    @Test(expected = SQLiteConstraintException::class)
    @Throws(Exception::class)
    fun exceptionInsertExistingUser() = runBlocking {
        val user = User(id = uuid4(), name = "Test User", email = "testuser@mail.de",
            password = "123", role = "standard")
        userDao.insert(user)
        userDao.insert(user)
    }

    @Test
    fun getUserWithDogsByIDTest() = runBlocking {
        val dogUserID = uuid4()
        val dogUser = User(id = dogUserID, name = "Dog User", email = "testuser@mail.de",
            password = "123", role = "standard")
        userDao.insert(dogUser)
        val dog1ID = uuid4()
        val dog1 = Dog(id = dog1ID, userID = dogUserID, name = "Luna")
        repository.insertDog(dog1)
        val dog2ID = uuid4()
        val dog2 = Dog(id = dog2ID, userID = dogUserID, name = "Rex")
        repository.insertDog(dog2)
        val dbUserWithDogs = userDao.getByIDWithDogs(dogUserID)
        assertThat(dbUserWithDogs).isNotNull()
        assertThat(dbUserWithDogs?.dogs).contains(dog1)
        assertThat(dbUserWithDogs?.dogs).contains(dog2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}