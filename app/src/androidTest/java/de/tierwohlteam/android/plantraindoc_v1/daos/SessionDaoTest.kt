package de.tierwohlteam.android.plantraindoc_v1.daos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.Session
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SessionDaoTest {
    private lateinit var sessionDao: SessionDao
    private lateinit var db: PTDdb
    private lateinit var repository: PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = PTDdb.getDatabase(context, test = true)
        repository = PTDRepository(context)
        sessionDao = db.sessionDao()
    }

    @Test
    fun insertAndGetSession(){
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        repository.insertGoal(goal)
        val planID = uuid4()
        val plan = Plan(id = planID, goalID = goalID)
        repository.insertPlan(plan)
        val sessionID = uuid4()
        val session = Session(id = sessionID, planID = planID, criterion = "Sit2 min")
        sessionDao.insert(session)
        val dbSession = sessionDao.getByID(sessionID)
        assertThat(dbSession).isEqualTo(session)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }
}
