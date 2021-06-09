package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.others.Event
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class GoalViewModel @Inject constructor(
    private val repository: PTDRepository,
    private val userID: Uuid,
) : ViewModel() {

    //observe the parent and if changed update the goals
    val parentGoal: MutableStateFlow<GoalWithPlan?> = MutableStateFlow(value = null)
    val selectedGoal: MutableStateFlow<GoalWithPlan?> = MutableStateFlow(value = null)
    val goals: StateFlow<List<GoalWithPlan>> = parentGoal.flatMapLatest {
        repository.getChildGoalsWithPlan(parent = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _insertGoalStatus = MutableLiveData<Event<Resource<Goal>>>(Event(Resource.empty()))
    val insertGoalStatus: LiveData<Event<Resource<Goal>>> = _insertGoalStatus

    /**
     * Save a new or updated goal to db
     */
    fun saveNewOrUpdatedGoal(goalText: String, description: String, status: String?) {
        if(goalText.isEmpty()) {
            _insertGoalStatus.postValue(Event(Resource.error("The fields must not be empty", null)))
            return
        }

        val newGoal = Goal(goal = goalText, description = description, parents = parentGoal.value?.goal?.id,
            userID = userID)
        if(status != null) newGoal.status = status
        viewModelScope.launch {
            repository.insertGoal(newGoal)
            _insertGoalStatus.postValue(Event(Resource.success(newGoal)))
        }
    }

    fun moveTreeDown(pos: Int) {
        parentGoal.value = goals.value[pos]
    }

    fun moveTreeUp() {
        viewModelScope.launch {
            parentGoal.value = repository.getParentGoalWithPlan(parentGoal.value)
        }
    }

    fun setSelectedGoal(goal:GoalWithPlan?){
        selectedGoal.value = goal
    }

}