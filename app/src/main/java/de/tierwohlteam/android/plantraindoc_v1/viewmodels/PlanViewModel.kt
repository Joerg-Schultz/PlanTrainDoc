package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.others.Event
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class PlanViewModel @Inject constructor(
    private val repository: PTDRepository,
    private val userID: Uuid,
) : ViewModel() {
    private var planHelper: PlanHelper? = null
    private var planConstraint: PlanConstraint? = null
    private lateinit var plan: Plan
    private lateinit var planID: Uuid

    private val _insertPlanStatus = MutableLiveData<Event<Resource<Plan>>>(Event(Resource.empty()))
    val insertPlanStatus: LiveData<Event<Resource<Plan>>> = _insertPlanStatus


    private fun checkGoal(goal: Goal?): Boolean{
        if(goal == null) {
            _insertPlanStatus.postValue(Event(Resource.error("No Goal for Plan", null)))
            return false
        }
        planID = uuid4()
        plan = Plan(id = planID, goalID = goal.id)
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
            planConstraint = PlanConstraint(type = type, value = value!!, planID = planID)
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
            planHelper = PlanHelper(planID = planID, type = type, value = value.toString())
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
            repository.insertPlan(plan)
            if(planConstraint != null) repository.insertPlanConstraint(planConstraint!!)
            if(planHelper != null) repository.insertPlanHelper(planHelper!!)
            _insertPlanStatus.postValue(Event(Resource.success(null)))
        }
    }
}