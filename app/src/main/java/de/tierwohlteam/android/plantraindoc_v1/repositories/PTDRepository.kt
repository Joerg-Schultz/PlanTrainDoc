package de.tierwohlteam.android.plantraindoc_v1.repositories

import android.content.Context
import androidx.annotation.WorkerThread
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc_v1.models.Dog
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.User

/**
 * Repository of the PlanTrainDoc Room database
 * use these functions for interaction with the db
 */

class PTDRepository(context: Context) {

    /**
     * User functions
     */
    private val userDao = PTDdb.getDatabase(context).userDao()
    /**
     *  Insert a User into the database
     *  @param[user] user object
     */
    //@WorkerThread
    fun insertUser(user: User) = userDao.insert(user)

    /**
     * get a user from the DB using its id
     * @param[userID] UUID id of the user
     * @return User? null if there is no user with this id
     */
    fun getUserByID(userID: Uuid) : User? = userDao.getByID(userID)

    /**
     * Dog functions
     */
    private val dogDao = PTDdb.getDatabase(context).dogDao()
    /**
     *  Insert a Dog into the database
     *  @param[dog] dog object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insertDog(dog: Dog) = dogDao.insert(dog)
    /**
     * get a dog from the DB using its id
     * @param[dogID] UUID id of the dog
     * @return Dog or null if there is no dog with this ID in the DB
     */
    fun getDogByID(dogID: Uuid) : Dog? = dogDao.getByID(dogID)

    /**
     * Goal functions
     */
    private val goalDao = PTDdb.getDatabase(context).goalDao()
    /**
     *  Insert a Goal into the database
     *  @param[goal] Goal object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun insertGoal(goal: Goal) = goalDao.insert(goal)
    /**
     * get a goal from the DB using its id
     * @param[goalID] UUID id of the goal
     * @return Goal or null if there is no dog with this ID in the DB
     */
    fun getGoalByID(goalID: Uuid) : Goal? = goalDao.getByID(goalID)

}
