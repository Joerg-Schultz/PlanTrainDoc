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


/**
 * A *Trial* is a single reinforceable behaviour
 *
 * @property[id] uuid, optional, will be generated
 * @property[success] boolean, was the behavior reinforced?
 * @property[criteria] list of the criteria currently type any, might change with db
 * @property[created] timestamp, generated
 */
@Entity(tableName = "trials",
foreignKeys = [ForeignKey(entity = Session::class,
parentColumns = arrayOf("id"),
childColumns = arrayOf("sessionID")
    )],
indices = arrayOf(Index("sessionID")))
@Serializable
data class Trial(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val sessionID: Uuid,
    val success: Boolean,
//    val criteria: List<String> = emptyList(),
    val created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
) {

}

@Entity(
    tableName = "trialCriteria",
    foreignKeys = [
        ForeignKey(
            entity = Trial::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("trialID")
        )],
    indices = [Index("trialID")]
)
@Serializable
data class TrialCriterion(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val trialID: Uuid,
    val criterion: String
)

data class TrialWithCriteria(
    @Embedded
    val trial: Trial,
    @Relation(
        parentColumn = "id",
        entityColumn = "trialID"
    )
    val criteria: List<TrialCriterion> = emptyList()
)