package de.tierwohlteam.android.plantraindoc.models

import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class TrialTest {
    @Test
    internal fun trialPropertiesTest(){
        val trial = Trial(success = true, sessionID = uuid4())
        val trialCriterion = TrialCriterion(trialID = trial.id ,criterion = "bla")
        assertThat(trial.success).isTrue()
        assertThat(trialCriterion.criterion).isEqualTo("bla")
    }
}