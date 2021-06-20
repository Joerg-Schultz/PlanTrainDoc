package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.models.PlanWithRelations
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanDao {

    @Insert
    suspend fun insert(plan: Plan)

    @Transaction
    @Insert
    suspend fun insert(planConstraint: PlanConstraint)

    @Transaction
    @Insert
    suspend fun insert(planHelper: PlanHelper)

    @Query("SELECT * from plans where id = :planID")
    suspend fun getByID(planID: Uuid) : Plan?

    @Transaction
    @Query("SELECT * from plans where id = :planID")
    suspend fun getByIDWithRelations(planID: Uuid): PlanWithRelations?

    @Transaction
    @Query("SELECT * from PlanHelpers where planID = :planID")
    suspend fun getHelperForPlan(planID: Uuid): PlanHelper?
    @Transaction
    @Query("SELECT * from PlanConstraints where planID = :planID")
    suspend fun getConstraintForPlan(planID: Uuid): PlanConstraint?

    @Transaction
    @Query("SELECT * from plans where id = :planID")
    fun getPlanWithRelationsFromPlan(planID: Uuid): Flow<PlanWithRelations?>

    @Query("DELETE from planconstraints where planID = :planID")
    suspend fun deleteConstraints(planID: Uuid)
    @Query("DELETE from planhelpers where planID = :planID")
    suspend fun deleteHelpers(planID: Uuid)
    @Delete
    suspend fun delete(plan: Plan)

    @Transaction
    suspend fun deleteWithHelpersAndConstraints(plan: Plan) {
        deleteConstraints(plan.id)
        deleteHelpers(plan.id)
        delete(plan)
    }

}
