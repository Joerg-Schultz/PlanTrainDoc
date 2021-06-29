package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.GoalTreeItem
import de.tierwohlteam.android.plantraindoc_v1.models.TrialWithAnnotations
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    var clickResetCounter: MutableStateFlow<Pair<Int, Int>?> = MutableStateFlow(value = null)
    var trialsFromPlan: MutableStateFlow<List<ChartPoint>> = MutableStateFlow(value = emptyList())


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
        clickResetCounter.value = Pair(totalClick,totalReset)
        trialsFromPlan.value = timeCourse
    }

}

data class ChartPoint(val xValue: Int, val yValue: Int, val sessionCriterion: String, val goal: String)