package de.tierwohlteam.android.plantraindoc.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc.models.Trial
import de.tierwohlteam.android.plantraindoc.models.TrialCriterion
import de.tierwohlteam.android.plantraindoc.models.TrialWithAnnotations
import de.tierwohlteam.android.plantraindoc.models.TrialWithCriteria
import kotlinx.coroutines.flow.Flow

@Dao
interface TrialDao {

    @Insert
    suspend fun insert(trial: Trial)

    @Insert
    suspend fun insert(trialCriterion: TrialCriterion)

    @Query("SELECT * from trials where id = :trialID")
    suspend fun getByID(trialID: Uuid): Trial?

    @Transaction
    @Query("SELECT * from trials where id = :trialID")
    suspend fun getByIDWithCriteria(trialID: Uuid): TrialWithCriteria?

    @Query("SELECT distinct trials.id, trials.success, trials.created," +
            "sessions.criterion as sessionCriterion, goals.goal as goal FROM " +
            "trials inner join sessions on trials.sessionID = sessions.id " +
            "inner join plans on sessions.planID = plans.id " +
            "inner join goals on plans.goalID = goals.id where " +
            "goals.id in (:goalsIDList)")
    fun getByGoalIDList(goalsIDList: List<Uuid>) : Flow<List<TrialWithAnnotations>>
}
