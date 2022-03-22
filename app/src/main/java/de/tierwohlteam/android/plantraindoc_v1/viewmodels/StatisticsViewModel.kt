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

    private var _sessionList = MutableStateFlow<List<Session>> (emptyList())


    private var _clickResetCounterGoalRecursive = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounterGoalRecursive: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounterGoalRecursive
    private var _clickResetCounterGoal = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounterGoal: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounterGoal
    private var _clickResetCounterSession = MutableStateFlow<Resource<Pair<Int, Int>?>> (Resource.loading(null))
    var clickResetCounterSession: StateFlow<Resource<Pair<Int, Int>?>> = _clickResetCounterSession

    private var _discreteValuesCounter = MutableStateFlow<Resource<Map<String,Pair<Int, Int>>>> (Resource.loading(null))
    var discreteValuesCounter: StateFlow<Resource<Map<String,Pair<Int, Int>>>> = _discreteValuesCounter

    private var _chartPoints = MutableStateFlow<Resource<List<ChartPoint>>> (Resource.loading(emptyList()))
    var chartPoints: StateFlow<Resource<List<ChartPoint>>> = _chartPoints
    private var _chartPointsTop = MutableStateFlow<Resource<List<ChartPoint>>> (Resource.loading(emptyList()))
    var chartPointsTop: StateFlow<Resource<List<ChartPoint>>> = _chartPointsTop

    private val trialsWithAnnotationGoalRecursive: StateFlow<List<TrialWithAnnotations>> = _goalList.flatMapLatest { goals ->
        repository.getTrialsByGoalIDList(goals.map { it.id })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue =emptyList()
    )
    private val trialsWithAnnotationGoal: StateFlow<List<TrialWithAnnotations>> = _goalList.flatMapLatest { goals ->
        repository.getTrialsByGoalIDList(goals.filter {it.level == 0}.map { it.id })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue =emptyList()
    )
    private val trialsWithAnnotationSessions: StateFlow<List<TrialWithAnnotations>> = _sessionList.flatMapLatest { sessions ->
        repository.getTrialsBySessionIDList(sessions.map { it.id })
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue =emptyList()
    )
    private val trialsWithCriteriaGoal: StateFlow<List<TrialWithCriteria>> = _goalList.flatMapLatest { goals ->
        val goalID = goals.firstOrNull { it.level == 0 }
        if (goalID == null) {
            emptyFlow()
        } else {
            repository.getTrialsWithCriteriaByGoalID(goalID.id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val trialsWithCriteriaSessions: StateFlow<List<TrialWithCriteria>> = _sessionList.flatMapLatest { sessions ->
        if (sessions.isEmpty()){
            emptyFlow()
        } else {
            repository.getTrialsWithCriteriaBySessionIDList(sessions.map { it.id })
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            trialsWithAnnotationGoalRecursive.collect {
                val (click, reset, timeCourse) = analyzeTrialList(it)
                _clickResetCounterGoalRecursive.value = Resource.success(Pair(click, reset))
                _chartPoints.value = Resource.success(timeCourse)
            }
        }
        viewModelScope.launch {
            trialsWithAnnotationGoal.collect {
                val (click, reset, timeCourse) = analyzeTrialList(it)
                _clickResetCounterGoal.value = Resource.success(Pair(click,reset))
                _chartPointsTop.value = Resource.success(timeCourse)
            }
        }
        viewModelScope.launch {
           trialsWithCriteriaGoal.collect {
               _discreteValuesCounter.value = Resource.success(analyzeCriteria(it))
           }
        }
    }


    fun setGoalList(goalList: List<GoalTreeItem>?) {
        _goalList.value = goalList ?: emptyList()
    }
    fun setSessionList(sessionList: List<Session>) {
        _sessionList.value = sessionList
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