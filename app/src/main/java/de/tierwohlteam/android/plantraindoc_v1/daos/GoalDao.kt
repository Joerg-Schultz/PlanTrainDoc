package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.*
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

    @Query("SELECT * from goals where parents = :parentID order by position")
    fun getChildrenByID(parentID: Uuid?): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * from goals where parents = :parentID order by position")
    fun getChildrenByIDWithPlan(parentID: Uuid?): Flow<List<GoalWithPlan>>

    @Query("SELECT * from goals where parents IS NULL order by position")
    fun getTopLevel(): Flow<List<Goal>>

    @Transaction
    @Query("SELECT * from goals where parents IS NULL order by position")
    fun getTopLevelWithPlan(): Flow<List<GoalWithPlan>>

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    // get all subgoals for a goal
    // returns GoalTreeItem
    @Transaction
    @Query("WITH RECURSIVE subGoals(id, goal, level) AS (" +
            "VALUES(:goalID,:goal,0)" +
            "    UNION ALL" +
            "    SELECT goals.id, goals.goal, subGoals.level+1\n" +
            "      FROM goals JOIN subGoals ON goals.parents=subGoals.id" +
            "     ORDER BY 2 DESC)" +
            "SELECT id, goal, level FROM subGoals")
    fun getSubGoalsRecursive(goalID: Uuid, goal:String): Flow<List<GoalTreeItem>>
}
