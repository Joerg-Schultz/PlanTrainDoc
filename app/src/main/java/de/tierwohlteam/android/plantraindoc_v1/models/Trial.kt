package de.tierwohlteam.android.plantraindoc_v1.models

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


/**
 * A *Trial* is a single reinforceable behaviour
 *
 * @property[id] uuid, optional, will be generated
 * @property[success] boolean, was the behavior reinforced?
 * @property[criteria] list of the criteria currently type any, might change with db
 * @property[created] timestamp, generated
 */
data class Trial(
    val id: Uuid = uuid4(),
    val sessionID: Uuid,
    val success: Boolean,
//    val criteria: List<String> = emptyList(),
    val created: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
) {

}
