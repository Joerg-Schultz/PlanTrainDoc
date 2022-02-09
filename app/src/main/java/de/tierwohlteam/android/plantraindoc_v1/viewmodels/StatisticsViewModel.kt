package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.others.Resource
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    private var _goalList = MutableStateFlow<List<GoalTreeItem>> (emptyList())
    var goalList: StateFlow<List<GoalTreeItem>> = _goalList
    private var _clickResetCounter = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounter: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounter
    private var _clickResetCounterTop = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounterTop: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounterTop
    private var _discreteValuesCounter = MutableStateFlow<Resource<Map<String,Pair<Int, Int>>>> (Resource.loading(null))
    var discreteValuesCounter: StateFlow<Resource<Map<String,Pair<Int, Int>>>> = _discreteValuesCounter
    private var _chartPoints = MutableStateFlow<Resource<List<ChartPoint>>> (Resource.loading(emptyList()))
    var chartPoints: StateFlow<Resource<List<ChartPoint>>> = _chartPoints
    private var _chartPointsTop = MutableStateFlow<Resource<List<ChartPoint>>> (Resource.loading(emptyList()))
    var chartPointsTop: StateFlow<Resource<List<ChartPoint>>> = _chartPointsTop


    fun setGoalList(goalList: List<GoalTreeItem>?) {
        _goalList.value = goalList ?: emptyList()
        analyzeGoalsNew()
    }

    private fun analyzeGoalsNew() {
        viewModelScope.launch {
            repository.getTrialsByGoalIDList(_goalList.value.map { it.id }).collect {
                val (click, reset, timeCourse) = analyzeTrialList(it)
                _clickResetCounter.value = Resource.success(Pair(click,reset))
                _chartPoints.value = Resource.success(timeCourse)
            }
        }
        viewModelScope.launch {
            repository.getTrialsByGoalIDList(_goalList.value.filter { it.level == 0 }.map { it.id }).collect {
                val (click, reset, timeCourse) = analyzeTrialList(it)
                _clickResetCounterTop.value = Resource.success(Pair(click,reset))
                _chartPointsTop.value = Resource.success(timeCourse)
            }
        }
        viewModelScope.launch {
            val goalID = goalList.value.firstOrNull { it.level == 0 }
            if (goalID == null) {
                _discreteValuesCounter.value = Resource.success(emptyMap())
            } else {
                repository.getTrialsWithCriteriaByGoalID(goalID.id).collect {
                    _discreteValuesCounter.value = Resource.success(analyzeCriteria(it))
                }
            }
        }
    }
    private fun analyzeTrialList(trialList: List<TrialWithAnnotations>): CombinedStats {
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
        return CombinedStats(totalClick, totalReset, timeCourse)
    }

    private fun analyzeCriteria(trialList: List<TrialWithCriteria>): Map<String, Pair<Int, Int>> {
        val result: MutableMap<String, Pair<Int,Int>> = mutableMapOf()
        for(trial in trialList) {
            val success = trial.trial.success
            for (criterion in trial.criteria) {
                val current = result.getOrDefault(criterion.criterion, Pair(0,0))
                val new = if (success) Pair(current.first + 1, current.second)
                else Pair(current.first, current.second + 1)
                result[criterion.criterion] = new
            }
        }
        return result
    }
}

data class ChartPoint(val xValue: Int, val yValue: Int, val sessionCriterion: String, val goal: String)
data class CombinedStats(val click: Int, val reset: Int, val timeCourse: List<ChartPoint>)