package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Trial
import de.tierwohlteam.android.plantraindoc_v1.models.TrialCriterion
import de.tierwohlteam.android.plantraindoc_v1.models.TrialWithCriteria

@Dao
interface TrialDao {

    @Insert
    fun insert(trial: Trial)

    @Insert
    fun insert(trialCriterion: TrialCriterion)

    @Query("SELECT * from trials where id = :trialID")
    fun getByID(trialID: Uuid): Trial?

    @Transaction
    @Query("SELECT * from trials where id = :trialID")
    fun getByIDWithCriteria(trialID: Uuid): TrialWithCriteria?
}
