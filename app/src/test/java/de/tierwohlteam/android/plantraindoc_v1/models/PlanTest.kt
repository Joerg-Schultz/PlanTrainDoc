package de.tierwohlteam.android.plantraindoc_v1.models


import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test
import kotlin.test.assertEquals


class PlanTest {
    @Test
    internal fun planPropertiesTest(){
        val userID = uuid4()
        val goal = Goal(goal = "Sit", userID = userID)
        val plan = Plan(goal = goal)
        val constraint = PlanConstraint(type = "time", value = 60)
        plan.constraint = constraint
        assertThat(plan.helpers).isEmpty()
        assertThat(plan.constraint).isEqualTo(constraint)
    }
}