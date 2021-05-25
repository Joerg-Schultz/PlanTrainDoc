package de.tierwohlteam.android.plantraindoc_v1

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import de.tierwohlteam.android.plantraindoc_v1.daos.DogDaoTest
import de.tierwohlteam.android.plantraindoc_v1.daos.UserDaoTest
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepositoryTest
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdbTest
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Suite
import java.io.IOException

@RunWith(Suite::class)
@Suite.SuiteClasses(PTDdbTest::class, UserDaoTest::class, DogDaoTest::class, PTDRepositoryTest::class)
class AllPTDdbTests {
    private lateinit var db: PTDdb
    private lateinit var repository: PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = PTDdb.getDatabase(context,test = true)
        repository = PTDRepository(context)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

}