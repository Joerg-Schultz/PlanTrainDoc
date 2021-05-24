package de.tierwohlteam.android.plantraindoc_v1.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class UserDaoTest {
    private lateinit var userDao: UserDao
    private lateinit var db: PTDdb

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, PTDdb::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
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
    //TODO what happens if there is no user in db
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}