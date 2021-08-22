package de.tierwohlteam.android.plantraindoc.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.plantraindoc.models.*
import de.tierwohlteam.android.plantraindoc.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc.repositories.PTDdb
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
class TrialDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testTrialDao")
    lateinit var trialDao:TrialDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @Test
    fun insertAndGetTrial() = runBlockingTest {
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
        db.close()
    }
}