package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * A PTD *Plan*
 * A plan sets the method I want to use to train a defined goal
 *
 * @property[id] UUID of the Plan, set autmatically
 * @property[goal] there are not plans without a goal
 * @property[created] timestamp, optional
 * @property[changed] timestamp, optional
 * @property[constraint] [PlanConstraint] object
 * @property[helpers] list of [PlanHelper]
 *
 * Does not have user as property. This is in the associated goal.
 * I might add it here later, if this makes syncing easier
 */
@Serializable
data class Plan(
    @Serializable(UUIDAsStringSerializer::class)
    val id: Uuid = uuid4(),
    @Contextual
    var created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    @Contextual
    var changed: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val goal: Goal,
    var constraint: PlanConstraint? = null,
    val helpers: List<PlanHelper> = listOf<PlanHelper>(),
) {
    companion object {
        const val path = "/plans"
    }
}

/**
 * Session can be restraint by
 * - time (train for 1 min)
 * - repetitions (train for 10 trials)
 */
@Serializable
data class PlanConstraint(val type: String,
                          val value: Int){
    companion object{
        const val time = "time"
        const val repetition = "repetition"
        const val open = ""
    }
}

/**
 * Session can have helpers
 * - duration
 * - distance
 * - alternative Signals
 * plus combinations
 */
@Serializable
data class PlanHelper(val type: String,
                      val value: String
){
    companion object {
        const val duration = "duration"
        const val distance = "distance"
        const val discrimination = "discrimination"
        const val cueIntroduction = "cue introduction"
        const val free = "free"
    }
}