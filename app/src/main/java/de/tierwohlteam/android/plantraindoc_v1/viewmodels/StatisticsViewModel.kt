package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.GoalTreeItem
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.models.TrialWithAnnotations
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    private var _clickResetCounter = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounter: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounter
    private var _discreteValuesCounter = MutableStateFlow<Resource<Map<String,Pair<Int, Int>>>> (Resource.loading(null))
    var discreteValuesCounter: StateFlow<Resource<Map<String,Pair<Int, Int>>>> = _discreteValuesCounter
    private var _trialsFromPlan = MutableStateFlow<Resource<List<ChartPoint>>> (Resource.loading(emptyList()))
    var trialsFromPlan: StateFlow<Resource<List<ChartPoint>>> = _trialsFromPlan


    fun analyzeGoals(goals: List<GoalTreeItem>, level: String = "all"){
        val selectedGoals: List<GoalTreeItem> = when(level){
            "top" -> goals.filter { it.level == 0 }
            else -> goals
        }
        viewModelScope.launch {
            repository.getTrialsByGoalIDList(selectedGoals.map { it.id }).collect {
                analyzeTrialList(it)
            }
        }
    }

     private fun analyzeTrialList(trialList: List<TrialWithAnnotations>) {
        var totalClick = 0
        var totalReset = 0
         var yPos = 0
        val timeCourse : MutableList<ChartPoint> = mutableListOf()
        for((xPos, trial) in trialList.sortedBy { it.created }.withIndex()){
            if(!trial.success) {
                totalReset++
            } else {
                totalClick++
                yPos++
            }
            timeCourse.add(ChartPoint(xPos, yPos, trial.sessionCriterion, trial.goal))
        }
        _clickResetCounter.value = Resource.success(Pair(totalClick,totalReset))
        _trialsFromPlan.value = Resource.success(timeCourse)
    }

    fun analyzePlan(plan: Plan) {
        viewModelScope.launch {
            repository.getTrialsWithCriteriaByPlan(plan).collect { trials ->
                val result: MutableMap<String, Pair<Int,Int>> = mutableMapOf()
                for(trial in trials) {
                    val success = trial!!.trial.success
                    for (criterion in trial.criteria) {
                        val current = result.getOrDefault(criterion.criterion, Pair(0,0))
                        val new = if (success) Pair(current.first + 1, current.second)
                            else Pair(current.first, current.second + 1)
                        result[criterion.criterion] = new
                    }
                }
                _discreteValuesCounter.value = Resource.success(result)
            }
        }
    }
}

data class ChartPoint(val xValue: Int, val yValue: Int, val sessionCriterion: String, val goal: String)