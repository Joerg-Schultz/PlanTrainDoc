package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class SessionDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testSessionDao")
    lateinit var sessionDao:SessionDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
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

    @Test
    fun insertAndGetSessionWithRelations() {
        val userID = uuid4()
        val user =
            User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
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
        //Insert Trial
        val trialID = uuid4()
        val trial = Trial(id = trialID, sessionID = sessionID, success = true)
        repository.insertTrial(trial)
        val dbSession = sessionDao.getByIDWithRelations(sessionID)
        assertThat(dbSession).isNotNull()
        assertThat(dbSession?.session).isEqualTo(session)
        assertThat(dbSession?.plan).isEqualTo(plan)
        assertThat(dbSession?.trials).contains(trial)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }
}
