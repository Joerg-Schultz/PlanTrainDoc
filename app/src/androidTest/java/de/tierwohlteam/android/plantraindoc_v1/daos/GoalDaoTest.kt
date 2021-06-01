package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalDependencyCrossRef
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
class GoalDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testGoalDao")
    lateinit var goalDao: GoalDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }


    @Test
    fun insertAndGetGoal() {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        goalDao.insert(goal)
        val dbGoal = goalDao.getByID(goalID)
        assertThat(dbGoal).isEqualTo(goal)
    }

    @Test
    internal fun insertAndGetGoalParentTest() {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val parentGoalID = uuid4()
        val childGoalID = uuid4()
        val parentGoal = Goal(id = parentGoalID, goal = "Sit", userID = userID)
        goalDao.insert(parentGoal)
        val childGoal = Goal(id = childGoalID, goal = "Sit 2 min", userID = userID,
            parents = parentGoalID)
        goalDao.insert(childGoal)
        val dbChildGoal = goalDao.getByIDWithRelations(childGoalID)
        assertThat(dbChildGoal).isNotNull()
        assertThat(dbChildGoal?.parent).isEqualTo(parentGoal)
    }

    @Test
    internal fun insertAndGetGoalDependencyTest() {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val dependentGoalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        goalDao.insert(goal)
        val dependentGoal = Goal(id = dependentGoalID, goal = "Sit 2 min", userID = userID)
        goalDao.insert(dependentGoal)
        goalDao.insertGoalDependency(GoalDependencyCrossRef(goalID,dependentGoalID))
        val dbGoal = goalDao.getByIDWithRelations(goalID)
        assertThat(dbGoal).isNotNull()
        assertThat(dbGoal?.goal).isEqualTo(goal)
        assertThat(dbGoal?.dependencies).contains(dependentGoal)
    }


    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }
}