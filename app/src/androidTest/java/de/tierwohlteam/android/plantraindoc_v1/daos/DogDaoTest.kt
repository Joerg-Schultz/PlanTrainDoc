package de.tierwohlteam.android.plantraindoc_v1.daos

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.Dog
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DogDaoTest {
    private lateinit var dogDao: DogDao
    private lateinit var db: PTDdb
    private lateinit var repository : PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = PTDdb.getDatabase(context, test = true)
        repository = PTDRepository(context)
        dogDao = db.dogDao()
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