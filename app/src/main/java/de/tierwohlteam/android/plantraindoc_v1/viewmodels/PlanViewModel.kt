package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.others.Event
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: PTDRepository,
) : ViewModel() {
    private var newPlanHelper: PlanHelper? = null
    private var newPlanConstraint: PlanConstraint? = null
    private lateinit var newPlan: Plan

    private val _insertPlanStatus = MutableLiveData<Event<Resource<Plan>>>(Event(Resource.empty()))
    val insertPlanStatus: LiveData<Event<Resource<Plan>>> = _insertPlanStatus


    private fun checkGoal(goal: Goal?): Boolean{
        if(goal == null) {
            _insertPlanStatus.postValue(Event(Resource.error("No Goal for Plan", null)))
            return false
        }
        newPlan = Plan(id = uuid4(), goalID = goal.id)
        return true
    }

    private fun checkConstraint(type: String, value: Int?): Boolean {
        if (type !in listOf(PlanConstraint.repetition, PlanConstraint.time, PlanConstraint.open)) {
            _insertPlanStatus.postValue(Event(Resource.error("Unknown type of Constraint", null)))
            return false
        }
        if (value == null && type != PlanConstraint.open) {
            _insertPlanStatus.postValue(Event(Resource.error("No Value for Constraint $type", null)))
            return false
        }
        if (type != PlanConstraint.open) {
            newPlanConstraint = PlanConstraint(type = type, value = value!!, planID = newPlan.id)
            return true
        }
        if(type == PlanConstraint.open){
            newPlanConstraint = null
            return true
        }
        _insertPlanStatus.postValue(Event(Resource.error("Unknown error for Constraint", null)))
        return false
    }

    private fun checkHelper(type: String, value: String?): Boolean {
        if (type !in listOf(
                PlanHelper.cueIntroduction, PlanHelper.discrimination,
                PlanHelper.duration, PlanHelper.distance, PlanHelper.free
            )
        ) {
            _insertPlanStatus.postValue(Event(Resource.error("Unknown type or Planhelper", null)))
            return false
        }
        if (value == null && type != PlanHelper.free) {
            _insertPlanStatus.postValue(Event(Resource.error("No value for $type", null)))
            return false
        }
        if ((type == PlanHelper.discrimination) && (value == null || value == "")) {
            _insertPlanStatus.postValue(Event(Resource.error("Need Signals for Discrimination", null)))
            return false
        }
        if (type != PlanHelper.free) {
            newPlanHelper = PlanHelper(planID = newPlan.id, type = type, value = value.toString())
            return true
        }
        if(type == PlanHelper.free){
            newPlanHelper = null
            return true
        }
        _insertPlanStatus.postValue(Event(Resource.error("Unknown error for Helper", null)))
        return false
    }

    suspend fun save(goal: Goal?,
                     constraintType: String, constraintValue: Int?,
                     helperType: String, helperValue: String?) {
        // && short circuits
        if(checkGoal(goal) && checkConstraint(constraintType, constraintValue)
            && checkHelper(helperType, helperValue)){
            repository.insertPlan(newPlan)
            if(newPlanConstraint != null) repository.insertPlanConstraint(newPlanConstraint!!)
            if(newPlanHelper != null) repository.insertPlanHelper(newPlanHelper!!)
            _insertPlanStatus.postValue(Event(Resource.success(null)))
        }
    }

    //Use PlanWithRelations instead
    // and save it as flowstate
    suspend fun getHelper(plan: Plan): PlanHelper? = repository.getPlanHelper(plan)
    suspend fun getConstraint(plan: Plan): PlanConstraint? = repository.getPlanConstraint(plan)
}