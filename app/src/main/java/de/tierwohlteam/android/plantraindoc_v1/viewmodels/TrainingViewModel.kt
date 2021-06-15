package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    private lateinit var session: Session

    private val selectedPlan: MutableStateFlow<Plan?> = MutableStateFlow(value = null)
    // TODO use _selectedPlan
    fun setSelectedPlan(plan: Plan){
        selectedPlan.value = plan
    }


    val sessionWithRelationsList:  StateFlow<List<SessionWithRelations>> = selectedPlan.flatMapLatest {
        repository.getSessionsWithRelationFromPlan(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val selectedPlanWithRelations: StateFlow<PlanWithRelations?> = selectedPlan.flatMapLatest {
        repository.getPlanWithRelationsFromPlan(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    suspend fun addTrial(success: Boolean) {
        val trial = Trial(sessionID = session.id, success = success)
        repository.insertTrial(trial)
    }

    suspend fun newSession() {
        if (selectedPlan.value == null) {
            //TODO Display message here
        } else {
            session = Session(planID = selectedPlan.value!!.id)
            repository.insertSession(session)
        }
    }
}