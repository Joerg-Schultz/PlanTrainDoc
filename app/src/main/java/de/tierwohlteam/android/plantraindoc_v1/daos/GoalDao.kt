package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalDependencyCrossRef
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithRelations
import kotlinx.coroutines.flow.Flow
import java.net.IDN

@Dao
interface GoalDao {

    @Insert
    fun insert(goal: Goal)

    @Insert
    fun insertGoalDependency(dependencyCrossRef: GoalDependencyCrossRef)

    @Query("SELECT * from goals where id = :goalID")
    fun getByID(goalID: Uuid): Goal?

    // Because Room runs the two queries for us under the hood,
    // add the @Transaction annotation, to ensure that this
    // happens atomically.
    @Transaction
    @Query("SELECT * from goals where id = :goalID")
    fun getByIDWithRelations(goalID: Uuid): GoalWithRelations?

    @Query("SELECT * from goals where parents = :parentID")
    fun getChildrenByID(parentID: Uuid?): Flow<List<Goal>>

    @Query("SELECT * from goals where parents IS NULL")
    fun getTopLevel(): Flow<List<Goal>>


}
