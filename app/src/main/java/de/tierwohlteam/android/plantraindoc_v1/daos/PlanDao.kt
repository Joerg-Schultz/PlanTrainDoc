package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.models.PlanWithRelations

@Dao
interface PlanDao {

    @Insert
    fun insert(plan: Plan)

    @Transaction
    @Insert
    fun insert(planConstraint: PlanConstraint)

    @Transaction
    @Insert
    fun insert(planHelper: PlanHelper)

    @Query("SELECT * from plans where id = :planID")
    fun getByID(planID: Uuid) : Plan?

    @Transaction
    @Query("SELECT * from plans where id = :planID")
    fun getByIDWithRelations(planID: Uuid): PlanWithRelations?
}