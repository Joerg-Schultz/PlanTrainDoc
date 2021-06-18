@file:UseSerializers(
    UUIDAsStringSerializer::class
)
package de.tierwohlteam.android.plantraindoc_v1.models

import androidx.room.*
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.math.roundToInt

/**
 * A PTD *Session*
 *
 * A session is a collection of trials with a single criterion
 *
 * @property[id] uuid, optional
 * @property[planID] the plan object this session is associated with
 * @property[criterion] what exactly should be clicked in this session
 */
@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Plan::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("planID")
        )
    ],
    indices = [Index("planID")]
)
@Serializable
data class Session(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val planID: Uuid,
    var created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    var criterion: String? = null,
    var comment: String? = null
) {
/*    /**
     * Return the percentage of successful trial as Int
     *
     * @return Int: percentage
     */
    fun resultPercentage() : Int {
        val successes = trials.count { it.success }
        val fails = trials.count{ !it.success }
        return if((successes+fails) == 0) 0 else
            ((successes.toFloat() / (successes + fails)) * 100).roundToInt()
    } */
}

data class SessionWithRelations(
    @Embedded
    val session:Session,
    @Relation(
        parentColumn = "planID",
        entityColumn = "id"
    )
    val plan: Plan,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionID",
        entity = Trial::class
    )
    val trials: List<Trial> = emptyList()
)