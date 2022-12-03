package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Dog

@Dao
interface DogDao {
    @Insert()
    suspend fun insert(dog: Dog)

    @Query("SELECT * from dogs where id = :dogID")
    fun getByID(dogID: Uuid): Dog?
}
