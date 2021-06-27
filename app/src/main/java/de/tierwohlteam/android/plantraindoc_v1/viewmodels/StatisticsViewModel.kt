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

    private lateinit var trainingList: List<SessionWithRelations>

    fun setTrainingList(trainingList: List<SessionWithRelations>) {
        this.trainingList = trainingList
        var totalClick = 0
        var totalReset = 0
        for(session in trainingList){
            totalClick += session.trials.filter { it.success }.size
            totalReset += session.trials.filter { !it.success }.size
        }
        clickResetCounter.value = Pair(totalClick,totalReset)
    }

}