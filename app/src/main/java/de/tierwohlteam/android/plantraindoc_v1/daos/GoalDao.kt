package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalDependencyCrossRef
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithRelations
import kotlinx.coroutines.flow.Flow
import java.net.IDN

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: Goal)

    @Insert
    suspend fun insertGoalDependency(dependencyCrossRef: GoalDependencyCrossRef)

    @Query("SELECT * from goals where id = :goalID")
    suspend fun getByID(goalID: Uuid): Goal?

    @Transaction
    @Query("SELECT * from goals where id = :goalID")
    suspend fun getByIDWithPlan(goalID: Uuid): GoalWithPlan?

    // Because Room runs the two queries for us under the hood,
    // add the @Transaction annotation, to ensure that this
    // happens atomically.
    @Transaction
    @Query("SELECT * from goals where id = :goalID")
    suspend fun getByIDWithRelations(goalID: Uuid): GoalWithRelations?

    @Query("SELECT * from goals where parents = :parentID")
    fun getChildrenByID(parentID: Uuid?): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * from goals where parents = :parentID")
    fun getChildrenByIDWithPlan(parentID: Uuid?): Flow<List<GoalWithPlan>>

    @Query("SELECT * from goals where parents IS NULL")
    fun getTopLevel(): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * from goals where parents IS NULL")
    fun getTopLevelWithPlan(): Flow<List<GoalWithPlan>>

    @Update
    suspend fun update(goal: Goal)


}
