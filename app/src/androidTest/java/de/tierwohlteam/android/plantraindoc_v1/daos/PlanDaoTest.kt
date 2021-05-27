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
class PlanDaoTest {
    private lateinit var planDao: PlanDao
    private lateinit var db: PTDdb
    private lateinit var repository: PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = PTDdb.getDatabase(context, test = true)
        repository = PTDRepository(context)
        planDao = db.planDao()
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
        //db.close()
    }
}