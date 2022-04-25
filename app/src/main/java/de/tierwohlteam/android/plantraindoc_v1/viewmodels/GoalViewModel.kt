package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.benasher44.uuid.Uuid
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.others.Constants
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.FIRST_GOAL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.FIRST_USAGE
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
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    //observe the parent and if changed update the goals
    private val parentGoal: MutableStateFlow<GoalWithPlan?> = MutableStateFlow(value = null)
    private val _selectedGoal: MutableStateFlow<GoalWithPlan?> = MutableStateFlow(value = null)
    val selectedGoal: StateFlow<GoalWithPlan?> = _selectedGoal

    val goals: StateFlow<Resource<List<GoalWithPlan>>> = parentGoal.flatMapLatest {
        repository.getChildGoalsWithPlan(parent = it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )

    val subGoalsRecursive: StateFlow<List<GoalTreeItem>?> = selectedGoal.flatMapLatest {
        repository.getSubGoalsRecursive(goal = it?.goal)
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
    fun saveNewOrUpdatedGoal(goalText: String, description: String, youtube: String, status: String?, goalWP: GoalWithPlan? = null) {
        if(goalText.isEmpty()) {
            _insertGoalStatus.postValue(Event(Resource.error("The fields must not be empty", null)))
            return
        }
        //update existing goal
        if(goalWP != null) {
            val goal: Goal = goalWP.goal
            goal.goal = goalText
            goal.description = description
            val regexMatchWeb = """^https://\S+?v=(\w+)""".toRegex().find(youtube)
            var videoID = regexMatchWeb?.groupValues?.last() ?: youtube
            val regexMatchShort = """https://youtu.be/(\w+)""".toRegex().find(youtube)
            videoID = regexMatchShort?.groupValues?.last() ?: videoID
            goal.youtube = videoID
            if (status != null) goal.status = status
            viewModelScope.launch {
                repository.updateGoal(goal)
                _insertGoalStatus.postValue(Event(Resource.success(goal)))
            }
        } else { // insert new goal
            val newGoal = Goal(
                goal = goalText, description = description, parents = parentGoal.value?.goal?.id,
                userID = userID, position = -1, youtube = youtube
            )
            if (status != null) newGoal.status = status
            viewModelScope.launch {
                repository.insertGoal(newGoal)
                _insertGoalStatus.postValue(Event(Resource.success(newGoal)))
            }
        }
    }

    fun moveTreeDown(pos: Int) {
        parentGoal.value = goals.value.data?.get(pos)
    }

    fun moveTreeUp() {
        viewModelScope.launch {
            parentGoal.value = repository.getParentGoalWithPlan(parentGoal.value)
        }
    }

    fun setSelectedGoal(goal:GoalWithPlan?){
        _selectedGoal.value = goal
    }

    suspend fun updatePositions(goalList: List<GoalWithPlan>) {
        for(i in goalList.indices){
            val goal = goalList[i].goal
            goal.position = i
            repository.updateGoal(goal)
        }
    }

    fun deleteGoal() {
        if (selectedGoal.value != null) {
            val goal = selectedGoal.value!!.goal
            val plan = selectedGoal.value!!.plan
            viewModelScope.launch {
                if (plan != null) {
                    val sessions = repository.getSessionsFromPlan(plan)
                    sessions.collect {
                        if (it.isNotEmpty()) {
                            _insertGoalStatus.postValue(Event(Resource.error("Can't delete goal with Session", null)))
                        } else {
                            repository.deletePlanWithHelpersAndConstraints(plan)
                            repository.deleteGoal(goal)
                            _insertGoalStatus.postValue(Event(Resource.success(null)))
                        }
                    }
                } else {
                    repository.deleteGoal(goal)
                    _insertGoalStatus.postValue(Event(Resource.success(null)))
                }
            }
        }
    }

    fun isTopLevel(): Boolean = parentGoal.value == null
}