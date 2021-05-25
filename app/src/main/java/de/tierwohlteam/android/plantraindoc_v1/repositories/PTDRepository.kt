package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.annotation.WorkerThread
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.User

class PTDRepository(context: Context) {

    private val userDao = PTDdb.getDatabase(context).userDao()
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insertUser(user: User) = userDao.insert(user)

    fun getUserByID(userID: Uuid) : User = userDao.getByID(userID)
}
