package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.os.CountDownTimer
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputEditText

import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {

    private lateinit var session: Session
    var totalTrials : MutableStateFlow<Int> = MutableStateFlow(value = 0)
    var countDown : MutableStateFlow<Int?> = MutableStateFlow(value = null)

    private val selectedPlan: MutableStateFlow<Plan?> = MutableStateFlow(value = null)
    private val selectedPlanConstraint: MutableStateFlow<PlanConstraint?> = MutableStateFlow(value = null)
    // TODO use _selectedPlan
    fun setSelectedPlan(plan: Plan){
        selectedPlan.value = plan
        viewModelScope.launch {
            selectedPlanConstraint.value = repository.getPlanConstraint(plan)
            if(selectedPlanConstraint.value != null){
                countDown.value = selectedPlanConstraint.value!!.value
            }
        }
    }

    val sessionWithRelationsList:  StateFlow<List<SessionWithRelations>> = selectedPlan.flatMapLatest {
        repository.getSessionsWithRelationFromPlan(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun addTrial(success: Boolean) {
        val trial = Trial(sessionID = session.id, success = success)
        repository.insertTrial(trial)
        totalTrials.value++
        if(countDown.value != null) countDown.value = countDown.value!! - 1
    }

    suspend fun newSession(criterion: String) {
        if (selectedPlan.value == null) {
            //TODO Display message here
        } else {
            session = Session(planID = selectedPlan.value!!.id, criterion = criterion)
            repository.insertSession(session)
        }
    }

    //start constraint timer
    fun sessionTimer() {
        if (selectedPlanConstraint.value != null && selectedPlanConstraint.value!!.type == PlanConstraint.time) {
            countDown.value = selectedPlanConstraint.value!!.value
            val timer = object : CountDownTimer(countDown.value!!.toLong() * 1000, 1000) {
                override fun onTick(p0: Long) {
                    countDown.value = countDown.value!! - 1
                }

                override fun onFinish() {
                }
            }
            timer.start()
        }
    }
}