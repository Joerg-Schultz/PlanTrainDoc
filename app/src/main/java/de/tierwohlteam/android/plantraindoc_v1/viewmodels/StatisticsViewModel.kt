package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.SessionWithRelations
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    var clickResetCounter: MutableStateFlow<Pair<Int, Int>?> = MutableStateFlow(value = null)
    var trialsFromPlan: MutableStateFlow<List<Triple<Int, Int, String>>> = MutableStateFlow(value = emptyList())


    private lateinit var trainingList: List<SessionWithRelations>

    fun setTrainingList(trainingList: List<SessionWithRelations>) {
        this.trainingList = trainingList
        var totalClick = 0
        var totalReset = 0
        var xPos = 0
        var yPos = 0
        val timeCourse : MutableList<Triple<Int, Int, String>> = mutableListOf()
        for(session in trainingList.sortedBy { it.session.created }){
            val criterion = session.session.criterion
            totalClick += session.trials.filter { it.success }.size
            totalReset += session.trials.filter { !it.success }.size
            for(trial in session.trials.sortedBy { it.created }){
                if(trial.success) yPos++
                timeCourse.add(Triple(xPos++, yPos, criterion ?: ""))
            }
        }
        clickResetCounter.value = Pair(totalClick,totalReset)
        trialsFromPlan.value = timeCourse
    }

}