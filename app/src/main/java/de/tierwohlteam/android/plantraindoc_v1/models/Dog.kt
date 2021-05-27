@file:UseSerializers(
    UUIDAsStringSerializer::class
)
package de.tierwohlteam.android.plantraindoc_v1.models

import kotlinx.serialization.UseSerializers
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable

/**
 * A PTD Dog
 * Every user can have one or more dogs
 */
@Entity(
    tableName = "dogs",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userID")
        )],
    indices = [Index(value = arrayOf("userID"))]
)
@Serializable
data class Dog(
    @PrimaryKey
    val id: Uuid = uuid4(),
    val name: String,
    val userID: Uuid) {

}
