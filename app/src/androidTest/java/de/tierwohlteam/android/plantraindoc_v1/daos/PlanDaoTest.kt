package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import de.tierwohlteam.android.plantraindoc_v1.models.*
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
class PlanDaoTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    @Inject
    @Named("testDB")
    lateinit var db: PTDdb
    @Inject
    @Named("testPlanDao")
    lateinit var planDao: PlanDao
    @Inject
    lateinit var repository: PTDRepository

    @Before
    internal fun setup() {
        hiltRule.inject()
    }

    @Test
    fun insertAndGetPlan() {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        repository.insertGoal(goal)
        val planID = uuid4()
        val plan = Plan(id = planID, goalID = goalID)
        planDao.insert(plan)
        val dbPlan = planDao.getByID(planID)
        assertThat(dbPlan).isEqualTo(plan)
    }

    @Test
    fun insertAndGetPlanWithRelations() {
        val userID = uuid4()
        val user = User(id = userID, name = "Test User", email = "testuser@mail.de", password = "123", role = "standard")
        repository.insertUser(user)
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        repository.insertGoal(goal)
        val planID = uuid4()
        val plan = Plan(id = planID, goalID = goalID)
        planDao.insert(plan)
        //Add Constraints
        val constraintID = uuid4()
        val planConstraint = PlanConstraint(id = constraintID, type = PlanConstraint.time,
            value = 60, planID = planID)
        planDao.insert(planConstraint)
        //Add Helper
        val helperID = uuid4()
        val planHelper = PlanHelper(id = helperID, planID = planID,
        type = PlanHelper.distance, value = "5")
        planDao.insert(planHelper)
        //Test Plan
        val planWithRelations: PlanWithRelations? = planDao.getByIDWithRelations(planID)
        assertThat(planWithRelations).isNotNull()
        assertThat(planWithRelations?.plan).isEqualTo(plan)
        //Test Constraint
        assertThat(planWithRelations?.constraint).isEqualTo(planConstraint)
        //Test Helper
        assertThat(planWithRelations?.helpers).contains(planHelper)
        //Test Goal
        assertThat(planWithRelations?.goal).isEqualTo(goal)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }
}