package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GoalTest {
    @Test
    internal fun goalTest(){
        val goalID = uuid4()
        val userID = uuid4() //add user object here??
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        assertThat(goal.status).isEqualTo(Goal.statusNew)
        assertThat(goal.parents).isNull()
    }

    @Test
    internal fun goalParentTest(){
        val parentGoalID = uuid4()
        val childGoalID = uuid4()
        val userID = uuid4() //add user object here??
        val parentGoal = Goal(id = parentGoalID, goal = "Sit", userID = userID)
        val childGoal = Goal(id = childGoalID, goal = "Sit 2 min", userID = userID, )
        val goalWithParent = GoalWithRelations(goal = childGoal, parent = parentGoal)
        assertThat(goalWithParent.parent).isEqualTo(parentGoal)
        assertThat(goalWithParent.dependencies).isEmpty()
    }

    @Test
    internal fun goalDependenciesTest(){
        val goalID = uuid4()
        val dependencyGoalID = uuid4()
        val userID = uuid4() //add user object here??
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        val dependencyGoal = Goal(id = dependencyGoalID, goal = "Down 2 min", userID = userID )
        val goalWithDependency = GoalWithRelations(goal = goal, dependencies = listOf(dependencyGoal))
        assertThat(goal.parents).isNull()
        assertThat(goalWithDependency.dependencies).contains(dependencyGoal)
    }

}