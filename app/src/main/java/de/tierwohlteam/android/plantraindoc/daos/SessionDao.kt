package de.tierwohlteam.android.plantraindoc.daos

import androidx.room.*
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc.models.Session
import de.tierwohlteam.android.plantraindoc.models.SessionWithRelations
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Dao
interface SessionDao {

    @Insert
    suspend fun insert(session: Session)

    @Query("SELECT * from sessions where id = :sessionID")
    suspend fun getByID(sessionID: Uuid): Session?

    @Transaction
    @Query("SELECT * from sessions where id = :sessionID")
    fun getByIDWithRelations(sessionID: Uuid): Flow<SessionWithRelations?>

    @Query("SELECT * from sessions where planID = :planID")
    fun getByPlanID(planID: Uuid?): Flow<List<Session>>

    @Query("SELECT * from sessions where planID = :planID")
    fun getByPlanIDWithRelations(planID: Uuid?) : Flow<List<SessionWithRelations>>

    @Query("UPDATE sessions set comment = :comment where id = :sessionID")
    suspend fun updateComment(sessionID: Uuid, comment: String?)

    @Transaction
    @Query("SELECT * from sessions")
    suspend fun getAllWithRelations(): List<SessionWithRelations>
    @Transaction
    @Query("SELECT * from sessions where created > :lastSyncDate")
    fun getNewWithRelations(lastSyncDate: LocalDateTime): List<SessionWithRelations>

}
