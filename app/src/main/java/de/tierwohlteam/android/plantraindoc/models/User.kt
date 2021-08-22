@file:UseSerializers(
    UUIDAsStringSerializer::class
)
package de.tierwohlteam.android.plantraindoc.models

//import java.util.*
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.serialization.*

/**
 * A PTD *User*
 *
 * Enable multiple users in PTD, Handle access rights
 *
 * @property[id] uuid, optional, will be generated if not provided
 * @property[name] Can not be empty
 * @property[email] Can not be empty, has to contain @
 * @property[password] currently no constraints, plain text. Adapt when adding authentication
 * @property[role] default standard, used to give access rights
 *
 */
@Entity(tableName = "users")
@Serializable
data class User(
    @PrimaryKey
    val id: Uuid = uuid4(),
    //val id: UUID = UUID.randomUUID(),
    val name: String,
    var email: String,
    var password: String, // TODO adapt when adding authentication
    var role: String = "standard"
) {
    init {
        if (name.isEmpty()) throw IllegalArgumentException("Need name for User")
        if (email.isEmpty()) throw IllegalArgumentException("Need E-Mail for User")
        //Minimal E-Mail validity check
        val emailRegex = "@".toRegex()
        if (!email.contains(emailRegex)) throw IllegalArgumentException("Illegal E-Mail for User")
    }
    companion object {
        const val path = "/user"
    }
}

data class UserWithDogs(
    @Embedded
    val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userID"
    )
    val dogs: List<Dog>
)