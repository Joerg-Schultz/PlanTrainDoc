@file:UseSerializers(
    UUIDAsStringSerializer::class
)
package de.tierwohlteam.android.plantraindoc.models

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * A PTD *Plan*
 * A plan sets the method I want to use to train a defined goal
 *
 * @property[id] UUID of the Plan, set autmatically
 * @property[created] timestamp, optional
 * @property[changed] timestamp, optional
 *
 * Does not have user as property. This is in the associated goal.
 * I might add it here later, if this makes syncing easier
 */
@Entity(tableName = "plans",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Goal::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("goalID")
        )
    ),
    indices = [Index("goalID")]
)
@Serializable
data class Plan(
    @PrimaryKey
    val id: Uuid = uuid4(),
    var created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    var changed: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val goalID: Uuid,
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
@Entity(
    tableName = "PlanConstraints",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("planID")
        )],
    indices = [Index("planID")]
)
@Serializable
data class PlanConstraint(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val planID: Uuid,
    val type: String,
    val value: Int
    ){
    companion object{
        const val time = "time"
        const val repetition = "repetition"
        const val open = ""
        const val defaultTime = 60
        const val defaultRepetition = 5
    }
}

/**
 * Session can have helpers
 * - duration
 * - distance
 * - alternative Signals
 * plus combinations
 */
@Entity(
    tableName = "PlanHelpers",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("planID")
        )],
    indices = [Index(value = arrayOf("planID"))]
)
@Serializable
data class PlanHelper(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val planID: Uuid,
    val type: String,
    val value: String
    ){
    companion object {
        const val duration = "duration"
        const val defaultDuration = 1.0f
        const val distance = "distance"
        const val defaultDistance = 3.0f
        const val discrimination = "discrimination"
        const val cueIntroduction = "cue introduction"
        const val defaultCueIntroduction = 50.0f
        const val free = "free"
    }
}

data class PlanWithRelations(
    @Embedded
    val plan: Plan,
    @Relation(
        parentColumn = "goalID",
        entityColumn = "id"
        )
    val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "planID"
    )
    val constraint: PlanConstraint?,
    @Relation(
        parentColumn = "id",
        entityColumn = "planID"
    )
    val helpers: List<PlanHelper> = emptyList()
)