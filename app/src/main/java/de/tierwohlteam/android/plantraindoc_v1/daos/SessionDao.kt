package de.tierwohlteam.android.plantraindoc_v1.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Session

@Dao
interface SessionDao {

    @Insert
    fun insert(session: Session)

    @Query("SELECT * from sessions where id = :sessionID")
    fun getByID(sessionID: Uuid): Session?

}
