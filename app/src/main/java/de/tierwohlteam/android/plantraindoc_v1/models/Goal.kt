@file:UseSerializers(
    UUIDAsStringSerializer::class,
)
package de.tierwohlteam.android.plantraindoc_v1.models

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import java.net.IDN


/**
 * A *Goal* is one goal behaviour. Can (and should) be broken down to trainable units
 *
 * @property[id] uuid, optional, will be generated
 * @property[goal] string, short title of goal
 * @property[description] longer explanation of behaviour, clear criteria
 * @property[parents] of whom this goal is a subgoal (can be null for top level goals) TODO only one
 * @property[position] the training order compared to its sister goals
 * @property[status] "new", "in progress", "finished", "stopped"
 * @property[created] timestamp for creation
 * @property[changed] timestamp for last modification
 * @property[userID] to whom this goal belongs
 */
@Entity(tableName = "goals",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userID")
        ),
        ForeignKey(
            entity = Goal::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("parents")
        )
    ),
    indices = [
        Index(value = arrayOf("userID")),
        Index(value = arrayOf("parents"))
    ]
)
@Serializable
data class Goal(
    @PrimaryKey
    var id: Uuid = uuid4(),
    var goal: String,
    var description: String = "",
    var parents: Uuid? = null, //implemented as list in KTor, but only first element is used
    var position: Int = 0, // ranking in relation so sisters
    var status: String = statusNew, //"in progress", "finished", "stopped"
    var created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    var changed: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val userID: Uuid,
    var youtube: String = ""
) {
    companion object {
        const val path = "/goals"
        const val statusNew = "new"
        const val statusInProgress = "in progress"
        const val statusFinished = "finished"
        const val statusStopped = "stopped"
    }
}

@Entity(primaryKeys = ["goalID", "dependentGoalID"],
    indices = [Index("dependentGoalID")]
)
data class GoalDependencyCrossRef(
    val goalID: Uuid,
    val dependentGoalID: Uuid
)

data class GoalWithRelations(
    @Embedded
    val goal: Goal,
    @Relation(
        parentColumn = "parents",
        entityColumn = "id"
    )
    val parent: Goal?,
    @Relation(
        parentColumn = "id",
        entity = Goal::class,
        entityColumn = "id",
        associateBy = Junction(value = GoalDependencyCrossRef::class,
            parentColumn = "goalID",
            entityColumn = "dependentGoalID")
    )
    val dependencies: List<Goal> = emptyList()
)

data class GoalWithPlan(
    @Embedded
    val goal: Goal,
    @Relation(
        parentColumn = "id",
        entityColumn = "goalID"
    )
    val plan: Plan?
)

data class GoalTreeItem(
    val id: Uuid,
    val goal: String,
    val level: Int
)