package de.tierwohlteam.android.plantraindoc.repositories

import android.util.Log
import androidx.annotation.WorkerThread
import com.benasher44.uuid.Uuid
import de.tierwohlteam.android.plantraindoc.daos.*
import de.tierwohlteam.android.plantraindoc.models.*
import de.tierwohlteam.android.plantraindoc.others.Resource
import de.tierwohlteam.android.plantraindoc.repositories.remote.PTDapi
import de.tierwohlteam.android.plantraindoc.repositories.remote.requests.AccountRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import javax.inject.Inject

/**
 * Repository of the PlanTrainDoc Room database
 * use these functions for interaction with the db
 */

class PTDRepository @Inject constructor(
    private val userDao: UserDao,
    private val dogDao: DogDao,
    private val goalDao: GoalDao,
    private val planDao: PlanDao,
    private val sessionDao: SessionDao,
    private val trialDao: TrialDao,
    private val ptdApi : PTDapi
) {

    /**
     * User functions
     *
     *  Insert a User into the database
     *  @param[user] user object
     */
    //@WorkerThread
    suspend fun insertUser(user: User) = userDao.insert(user)

    /**
     * get a user from the DB using its id
     * @param[userID] UUID id of the user
     * @return User? null if there is no user with this id
     */
    suspend fun getUserByID(userID: Uuid) : User? = userDao.getByID(userID)

    /**
     * Get all users from the db
     * needed to check if the androiduser is in the db
     * after destructive db migration
     */
    suspend fun getUsers(): List<User> = userDao.getAll()

    /**
     * Dog functions
     *
     *  Insert a Dog into the database
     *  @param[dog] dog object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertDog(dog: Dog) = dogDao.insert(dog)
    /**
     * get a dog from the DB using its id
     * @param[dogID] UUID id of the dog
     * @return Dog or null if there is no dog with this ID in the DB
     */
    suspend fun getDogByID(dogID: Uuid) : Dog? = dogDao.getByID(dogID)

    /**
     * Goal functions
     ***********************************************************
     *  Insert a Goal into the database
     *  @param[goal] Goal object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertGoal(goal: Goal) = goalDao.insert(goal)

    /**
     * Update existing goal
     * @param[goal] goal object existing in db
     */
    suspend fun updateGoal(goal: Goal) = goalDao.update(goal)

    /**
     * delete a goal
     * @param[goal] Goal object
     */
    suspend fun deleteGoal(goal: Goal) = goalDao.delete(goal)
    /**
     * get a goal from the DB using its id
     * @param[goalID] UUID id of the goal
     * @return Goal or null if there is no dog with this ID in the DB
     */
    suspend fun getGoalByID(goalID: Uuid) : Goal? = goalDao.getByID(goalID)

    /**
     * get a list of all children of a goal
     * @param[parent] the parent goal
     * @return List of child goals, empty list if there are none
     */
    fun getChildGoals(parent: Goal?) : Flow<List<Goal>> =
        if(parent != null) goalDao.getChildrenByID(parent.id) else goalDao.getTopLevel()

    /**
     * get a list of all children of a goal with associated plans
     * @param[parent] GoalWithPlan
     * @return List of child goalWithPlan, empty list if there are none
     */
    @OptIn(InternalCoroutinesApi::class)
    fun getChildGoalsWithPlan(parent: GoalWithPlan?) : Flow<Resource<List<GoalWithPlan>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = if (parent != null) goalDao.getChildrenByIDWithPlan(parent.goal.id) else
            goalDao.getTopLevelWithPlan()
        emitAll(dataFlow.map { Resource.success(it) })
    }

    /**
     * get all goals which were changed since a given date
     * @param[lastSyncDate] LocalDateTime?
     *                      if null, all goals will be returned
     * @return List<Goal>
     */
    suspend fun getNewGoalsLocal(lastSyncDate: LocalDateTime? = null): List<Goal> {
        return if (lastSyncDate == null) {
            goalDao.getAll()
        } else {
            goalDao.getNew(lastSyncDate)
        }
    }


/**
     * get the parent of a goal
     * @param[goal] the child goal
     * @return Goal parent or null if goal already is top level
     */
    suspend fun getParentGoal(goal: Goal?): Goal? = goal?.parents?.let { goalDao.getByID(it) }

    /**
     * get the parent of a goalWithPlan
     * @param[goal] GoalWithPlan the child goal
     * @return GoalWithPlan parent or null if goal already is top level
     */
    suspend fun getParentGoalWithPlan(goalWithPlan: GoalWithPlan?): GoalWithPlan? =
        goalWithPlan?.goal?.parents?.let { goalDao.getByIDWithPlan(it) }

    /**
     * recursively get all sub Goals for a goal
     * @param[goal] Goal
     * @return List of GoalTreeItem
     */
    fun getSubGoalsRecursive(goal: Goal?): Flow<List<GoalTreeItem>> {
        return if (goal == null) {
            flowOf(emptyList())
        } else {
            goalDao.getSubGoalsRecursive(goal.id, goal.goal)
        }
    }

    /**
     * Plan functions
     *
     *  Insert a Plan into the database
     *  @param[plan] Plan object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPlan(plan: Plan) = planDao.insert(plan)
    /**
     * delete a plan with constraint and helper
     * only works if the plan has no sessions
     * //TODO Can I make this a transaction?
     */
    suspend fun deletePlanWithHelpersAndConstraints(plan: Plan){
        planDao.deleteWithHelpersAndConstraints(plan)
    }

    /**
     * insert plan with constraint and helpers
     * @param[planWR] PlanWithRelations
     */
    suspend fun insertPlanWithRelations(planWR: PlanWithRelations) {
        insertPlan(planWR.plan)
        if(planWR.constraint != null) {
            insertPlanConstraint(planWR.constraint)
        }
        planWR.helpers.forEach { helper ->
            insertPlanHelper(helper)
        }
    }
    /**
     * Insert a Constraint for a Plan
     * @param[constraint] a PlanConstraint object
     */
    suspend fun insertPlanConstraint(constraint: PlanConstraint) =
        planDao.insert(constraint)
    /**
     * Insert a Helper for a Plan
     * @param[helper] a PlanHelper object
     */
    suspend fun insertPlanHelper(helper: PlanHelper) =
        planDao.insert(helper)

    /**
     * Get the Helper for a Plan (currently only one)
     * @param[plan] Plan object
     * @return the Helper or null
     */
    suspend fun getPlanHelper(plan: Plan): PlanHelper? = planDao.getHelperForPlan(plan.id)

    /**
     * Get the Constraint for a Plan (currently only one)
     * @param[plan] Plan object
     * @return the Constraint or null
     */
    suspend fun getPlanConstraint(plan: Plan): PlanConstraint? = planDao.getConstraintForPlan(plan.id)


    /**
     * get a plan from the DB using its id
     * @param[planID] UUID id of the plan
     * @return Plan or null if there is no dog with this ID in the DB
     */
    suspend fun getPlanByID(planID: Uuid) : Plan? = planDao.getByID(planID)

    /**
     * get a plan from the DB using its id
     * @param[planID] UUID id of the plan
     * @return Plan or null if there is no dog with this ID in the DB
     */
    fun getPlanWithRelationsFromPlan(plan: Plan?) : Flow<PlanWithRelations?> =
        if (plan == null) flowOf(null) else planDao.getPlanWithRelationsFromPlan(planID = plan.id)

    /**
     * get all plans with relations which were changed since a given date
     * @param[lastSyncDate] LocalDateTime?
     *                      if null, all goals will be returned
     * @return List<PlanWithRelation>
     */
    suspend fun getNewPlansWithRelationsLocal(lastSyncDate: LocalDateTime? = null): List<PlanWithRelations> {
        return if (lastSyncDate == null) {
            planDao.getAllWithRelations()
        } else {
            planDao.getNewWithRelations(lastSyncDate)
        }
    }

    /**
     * Session functions
     *
     *  Insert a Session into the database
     *  @param[session] Session object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertSession(session: Session) = sessionDao.insert(session)
    /**
     * get a session from the DB using its id
     * @param[sessionID] UUID id of the session
     * @return Session or null if there is no dog with this ID in the DB
     */
    suspend fun getSessionByID(sessionID: Uuid) : Session? = sessionDao.getByID(sessionID)

    /**
     * get a session with its relations
     * @param[sessionID]
     * @return SessionWithRelations
     */
    fun getSessionWithRelationByID(sessionID: Uuid?): Flow<SessionWithRelations?> =
        if(sessionID == null) flowOf(null) else sessionDao.getByIDWithRelations(sessionID)

    /**
     * get all sessions with relations (Trials!) which were changed since a given date
     * @param[lastSyncDate] LocalDateTime?
     *                      if null, all goals will be returned
     * @return List<SessionWithRelations>
     */

    suspend fun getNewSessionsWithRelationsLocal(lastSyncDate: LocalDateTime?): List<SessionWithRelations> {
        return if (lastSyncDate == null) {
            sessionDao.getAllWithRelations()
        } else {
            sessionDao.getNewWithRelations(lastSyncDate)
        }
    }

    /**
     * get all session associated with a plan
     * @param[plan] Plan object
     * @return a flow of the sessions
     */
    fun getSessionsFromPlan(plan: Plan?) : Flow<List<Session>> =
        sessionDao.getByPlanID(plan?.id)

    /**
     * get all sessions with Relations associated with a plan
     * @param[plan] Plan object
     * @return a flow of SessionWithRelation
     */
    fun getSessionsWithRelationFromPlan(plan: Plan?): Flow<List<SessionWithRelations>> {
        return if(plan == null) flowOf(emptyList()) else sessionDao.getByPlanIDWithRelations(plan.id)
    }

    /**
     * update a comment for a session
     */
    suspend fun updateCommentInSession(session: Session?){
        if(session != null)
        sessionDao.updateComment(session.id, session.comment)
    }

    /**
     * Trial functions
     *
     *  Insert a Trial into the database
     *  @param[trial] Trial object
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTrial(trial: Trial) = trialDao.insert(trial)

    /**
     * get a trial from the DB using its id
     * @param[trialID] UUID id of the trial
     * @return Trial or null if there is no dog with this ID in the DB
     */
    suspend fun getTrialByID(trialID: Uuid) : Trial? = trialDao.getByID(trialID)

    /**
     * save a trial criterion in the db
     * @param[trialCriterion] TrialCriterion object
     */
    suspend fun insertTrialCriterion(trialCriterion: TrialCriterion) = trialDao.insert(trialCriterion)

    /**
     * get all Trials with session criterion and goal
     * @param[goalsIDList] list of ids of goals
     * @return Flow of List of TrialWithAnnotations
     */
    fun getTrialsByGoalIDList(goalsIDList: List<Uuid>) : Flow<List<TrialWithAnnotations>> =
        trialDao.getByGoalIDList(goalsIDList)


    /**
     * **********************************************************************
     * Interaction with PTD Server
     * **********************************************************************
     *
     */

    /**
     * register a new user
     * @param[id] Uuid of the user (from Room)
     * @param[name] name of user
     * @param[eMail] email
     * @param[password]
     */
    suspend fun register(id:Uuid, name: String, eMail: String, password: String) =
        withContext(Dispatchers.IO){
            try {
                val response = ptdApi.register(
                    AccountRequest(name = name, eMail = eMail, password = password, id = id.toString()))
                if(response.isSuccessful && response.body()!!.successful){
                    Resource.success(response.body()?.message)
                } else {
                    Resource.error(response.body()?.message ?: response.message(), null)
                }
            } catch (e: Exception) {
                Resource.error("Couldn't connect to PlanTrainDoc Web Server", null)
            }
        }

    /**
     * login a user
     * @param[id] Uuid of the user (from Room)
     * @param[name] name of user
     * @param[eMail] email
     * @param[password]
     */
    suspend fun login(id:Uuid, name: String, eMail: String, password: String) =
        withContext(Dispatchers.IO){
            try {
                val response = ptdApi.login(
                    AccountRequest(name = name, eMail = eMail, password = password, id = id.toString()))
                if(response.isSuccessful && response.body()!!.successful){
                    Resource.success(response.body()?.message)
                } else {
                    Resource.error(response.body()?.message ?: response.message(), null)
                }
            } catch (e: Exception) {
                Resource.error("Couldn't connect to PlanTrainDoc Web Server", null)
            }
        }

    /**
     * get all goals from Web Server which were changed since a given date
     * @param[lastSyncDate] LocalDateTime?
     *                      if null, all goals will be returned
     * @return List<Goal>
     */
    //TODO catch errors in all get functions return SimpleResponse with List<Goal>
    suspend fun getNewGoalsRemote(lastSyncDate: LocalDateTime?): List<Goal> =
        withContext(Dispatchers.IO){
            ptdApi.goals(date = lastSyncDate?.toString() ?: "")

         /*   try {
                ptdApi.goals(date = lastSyncDate?.toString() ?: "")
            } catch(e: Exception){
                emptyList<Goal>()
            } */
        }

    /**
     * Send a list of local goals to the server and insert it to DB
     * @param[localGoals] List of Goal
     * @return Resource<SimpleResponse<String>>
     */
    suspend fun putGoalsRemote(localGoals: List<Goal>) =
        withContext(Dispatchers.IO){
            try {
                val response = ptdApi.insertGoal(localGoals)
                if(response.isSuccessful && response.body()!!.successful){
                    Resource.success(response.body()?.message)
                } else {
                    Resource.error(response.body()?.message ?: response.message(), null)
                }
            } catch (e: Exception) {
                Resource.error("Couldn't connect to PlanTrainDoc Web Server", null)
            }
        }

    /**
     * Send a list of local PlanWithRelations to the server and insert it to DB
     * @param[localPWRList] List of PlanWithRelations
     * @return Resource<SimpleResponse<String>>
     */
    suspend fun putPlansRemote(localPWRList: List<PlanWithRelations>) =
        withContext(Dispatchers.IO) {
        try {
            Log.d("PLANSYNC", "in PutPlansRemote: $localPWRList")
            val response = ptdApi.insertPlans(localPWRList)
            if(response.isSuccessful && response.body()!!.successful){
                Resource.success(response.body()?.message)
            } else {
                Resource.error(response.body()?.message ?: response.message(), null)
            }
        } catch (e: Exception) {
            Resource.error("Couldn't connect to PlanTrainDoc Web Server", null)
        }
    }

    /**
     * get all plans with their relations from Web Server which were changed since a given date
     * @param[lastSyncDate] LocalDateTime?
     *                      if null, all goals will be returned
     * @return List<PlanWithRelations>
     */
    suspend fun getNewPlansWithRelationsRemote(lastSyncDate: LocalDateTime?): List<PlanWithRelations> =
        withContext(Dispatchers.IO) {
            ptdApi.plans(date = lastSyncDate?.toString() ?: "")
        }

    /**
     * Send a list of local SessionWithRelations to the server and insert it to DB
     * @param[localSWRList] List of SessionWithRelations
     * @return Resource<SimpleResponse<String>>
     */
    suspend fun putSessionsRemote(newSessions: List<SessionWithRelations>) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("PLANSYNC", "in PutPlansRemote: $newSessions")
                val response = ptdApi.insertSessions(newSessions)
                if (response.isSuccessful && response.body()!!.successful) {
                    Resource.success(response.body()?.message)
                } else {
                    Resource.error(response.body()?.message ?: response.message(), null)
                }
            } catch (e: Exception) {
                Resource.error("Couldn't connect to PlanTrainDoc Web Server", null)
            }
        }
    }
}
