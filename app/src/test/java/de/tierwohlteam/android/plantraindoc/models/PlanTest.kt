package de.tierwohlteam.android.plantraindoc.models


import com.benasher44.uuid.uuid4
import kotlin.test.Test


class PlanTest {
    @Test
    internal fun planPropertiesTest(){
        val userID = uuid4()
        val goalID = uuid4()
        val goal = Goal(id = goalID, goal = "Sit", userID = userID)
        val planID = uuid4()
        val plan = Plan(id = planID, goalID = goalID)
        val constraintID = uuid4()
        val constraint = PlanConstraint(id = constraintID, type = "time", value = 60,planID = planID)
    }
}