package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.uuid4
import com.google.common.truth.Truth.assertThat
import kotlin.test.Test

class TrialTest {
    @Test
    internal fun trialPropertiesTest(){
        val trial = Trial(success = true, sessionID = uuid4())
        assertThat(trial.success).isTrue()
    }
}