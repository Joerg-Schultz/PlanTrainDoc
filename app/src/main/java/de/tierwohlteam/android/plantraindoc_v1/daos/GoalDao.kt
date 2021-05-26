package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Goal

@Dao
interface GoalDao {

    @Insert
    fun insert(goal: Goal)

    @Query("SELECT * from goals where id = :goalID")
    fun getByID(goalID: Uuid): Goal?

}
