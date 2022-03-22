package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.*
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

    @Query("SELECT distinct trials.id, trials.success, trials.created," +
            "sessions.criterion as sessionCriterion, goals.goal as goal FROM " +
            "trials inner join sessions on trials.sessionID = sessions.id " +
            "inner join plans on sessions.planID = plans.id " +
            "inner join goals on plans.goalID = goals.id where " +
            "sessions.id in (:sessionIDList)")
    fun getBySessionIDList(sessionIDList: List<Uuid>) : Flow<List<TrialWithAnnotations>>

/*    @Query("SELECT * FROM trials " +
            "inner join sessions on trials.sessionID = sessions.id " +
            "inner join plans on sessions.planID = :planID")
    fun getWithCriteriaByPlan(planID: Uuid): Flow<List<TrialWithCriteria>> */

    @Transaction
    @Query("SELECT distinct trials.* from trials, sessions WHERE trials.sessionID = sessions.id " +
            "and sessions.planID = :planID")
    fun getWithCriteriaByPlan(planID: Uuid): Flow<List<TrialWithCriteria>>

    @Transaction
    @Query("SELECT distinct trials.* from trials WHERE trials.sessionID in (:sessionIDList)")
    fun getWithCriteriaBySession(sessionIDList: List<Uuid>): Flow<List<TrialWithCriteria>>

    @Transaction
    @Query("SELECT distinct trials.* from trials, sessions, plans WHERE trials.sessionID = sessions.id " +
            "and sessions.planID = plans.id " +
            "and plans.goalID = :goalID")
    fun getWithCriteriaByGoalID(goalID: Uuid): Flow<List<TrialWithCriteria>>

}
