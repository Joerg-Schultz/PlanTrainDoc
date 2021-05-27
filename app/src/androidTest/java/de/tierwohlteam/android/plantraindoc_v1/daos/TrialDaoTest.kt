package de.tierwohlteam.android.plantraindoc_v1.daos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TrialDaoTest {
    private lateinit var trialDao: TrialDao
    private lateinit var db: PTDdb
    private lateinit var repository: PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = PTDdb.getDatabase(context, test = true)
        repository = PTDRepository(context)
        trialDao = db.trialDao()
    }

    @Test
    fun insertAndGetTrial(){
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
        repository.insertSession(session)
        val trialID = uuid4()
        val trial = Trial(id = trialID, sessionID = sessionID, success = true)
        trialDao.insert(trial)
        val dbTrial = trialDao.getByID(trialID)
        assertThat(dbTrial).isEqualTo(trial)
    }

    @Test
    fun insertAndGetTrialWithCriteria(){
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
        repository.insertSession(session)
        val trialID = uuid4()
        val trial = Trial(id = trialID, sessionID = sessionID, success = true)
        trialDao.insert(trial)
        val trialCriterion = TrialCriterion(trialID = trialID, criterion = "bla")
        trialDao.insert(trialCriterion)
        val dbTrialWithCriteria = trialDao.getByIDWithCriteria(trialID)
        assertThat(dbTrialWithCriteria?.trial).isEqualTo(trial)
        assertThat(dbTrialWithCriteria?.criteria).contains(trialCriterion)
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }
}