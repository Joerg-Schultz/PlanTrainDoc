package de.tierwohlteam.android.plantraindoc.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc.models.Dog

@Dao
interface DogDao {
    @Insert()
    suspend fun insert(dog: Dog)

    @Query("SELECT * from dogs where id = :dogID")
    suspend fun getByID(dogID: Uuid): Dog?
}
