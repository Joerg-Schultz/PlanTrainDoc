package de.tierwohlteam.android.plantraindoc_v1.daos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*
import com.google.common.truth.Truth.assertThat
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithRelations
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDdb
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class GoalDaoTest {
    private lateinit var goalDao: GoalDao
    private lateinit var db: PTDdb
    private lateinit var repository: PTDRepository

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = PTDdb.getDatabase(context, test = true)
        repository = PTDRepository(context)
        goalDao = db.goalDao()
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

    @After
    @Throws(IOException::class)
    fun closeDb() {
        //db.close()
    }
}