package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.models.PlanWithRelations
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Dao
interface PlanDao {

    @Insert
    suspend fun insert(plan: Plan)

    // Postgres has only a single entry for constraints with the same value
    // This throws an error when inserting an already existing constraint
    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
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

    @Transaction
    @Query("SELECT * from plans")
    suspend fun getAllWithRelations(): List<PlanWithRelations>
    @Transaction
    @Query("SELECT * from plans where changed > :lastSyncDate")
    fun getNewWithRelations(lastSyncDate: LocalDateTime): List<PlanWithRelations>

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
