package de.tierwohlteam.android.plantraindoc.models

import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class SessionTest {
    @Test
    internal fun sessionPropertiesTest(){
        val goal = Goal(goal = "Sit", userID = uuid4()
        )
        val sessionPlan = Plan(goalID = goal.id)
        val session = Session(planID = sessionPlan.id)
        assertThat(session.criterion).isNull()
    }
}