package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.User
import de.tierwohlteam.android.plantraindoc_v1.models.UserWithDogs

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * from users where id = :userID")
    suspend fun getByID(userID: Uuid): User?

    @Transaction
    @Query("SELECT * from users where id = :userID")
    suspend fun getByIDWithDogs(userID: Uuid): UserWithDogs?

}