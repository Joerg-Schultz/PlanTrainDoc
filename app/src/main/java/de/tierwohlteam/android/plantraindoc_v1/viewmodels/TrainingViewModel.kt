package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.models.Session
import de.tierwohlteam.android.plantraindoc_v1.models.SessionWithRelations
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    fun sessionWithRelationsList(goalWithPlan: MutableStateFlow<GoalWithPlan?>): StateFlow<List<SessionWithRelations>> =
        goalWithPlan.flatMapLatest {
        repository.getSessionsWithRelationFromPlan(it?.plan)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}