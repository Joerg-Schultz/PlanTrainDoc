package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable


/**
 * A *Goal* is one goal behaviour. Can (and should) be broken down to trainable units
 *
 * @property[id] uuid, optional, will be generated
 * @property[goal] string, short title of goal
 * @property[description] longer explanation of behaviour, clear criteria
 * @property[parents] of whom this goal is a subgoal (can be null for top level goals) TODO only one
 * @property[dependencies] non sister goals which are needed for this goal
 * @property[position] the training order compared to its sister goals
 * @property[status] "new", "in progress", "finished", "stopped"
 * @property[created] timestamp for creation
 * @property[changed] timestamp for last modification
 * @property[user] to whom this goal belongs
 */

@Serializable
data class Goal(
    @Serializable(UUIDAsStringSerializer::class)
    var id: Uuid = uuid4(),
    var goal: String,
    var description: String = "",
    var parents: List<Goal> = emptyList(), //I am currently using only the first element. Tree, no DAG
    var dependencies: List<Goal> = emptyList(),
    var position: Int = 0, // ranking in relation so sisters
    var status: String = statusNew, //"in progress", "finished", "stopped"
    var created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    var changed: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.UTC),
    val user: User
) {
    companion object {
        const val path = "/goals"
        const val statusNew = "new"
        const val statusInProgress = "in progress"
        const val statusFinished = "finished"
        const val statusStopped = "stopped"
    }
}
