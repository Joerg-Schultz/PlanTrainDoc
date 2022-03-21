package de.tierwohlteam.android.plantraindoc_v1.viewmodels

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.random.Random

@ExperimentalCoroutinesApi
@HiltViewModel
class TrainingViewModel @Inject constructor(
    private val repository: PTDRepository,
)  : ViewModel() {
    @Inject
    @Named("DistanceScheme")
    lateinit var distanceScheme : ReinforcementScheme
    @Inject
    @Named("DurationScheme")
    lateinit var durationScheme : ReinforcementScheme

    private lateinit var session: Session
    private lateinit var constraintTimer: CountDownTimer
    private var currentHelperValue: String? = null

    // TODO use non Mutables for interaction with outside world
    var totalTrials : MutableStateFlow<Int> = MutableStateFlow(value = 0)
    var countDown : MutableStateFlow<Int?> = MutableStateFlow(value = null)
    var clickResetCounter: MutableStateFlow<Pair<Int,Int>> = MutableStateFlow(value = Pair(0,0))
    //var helperNextValue: MutableStateFlow<String?> = MutableStateFlow(value = null)
    var helperNextValue: MutableSharedFlow<String?> = MutableSharedFlow(replay = 1)
    var sessionType: MutableStateFlow<String?> = MutableStateFlow(value = null)
    var helperValueList: List<String> = emptyList()

    private val selectedPlan: MutableStateFlow<Plan?> = MutableStateFlow(value = null)
    private val selectedPlanConstraint: MutableStateFlow<PlanConstraint?> = MutableStateFlow(value = null)
    private val selectedPlanHelper: MutableStateFlow<PlanHelper?> = MutableStateFlow(value = null)
    private var getHelperNextValue : (() -> String)? = null

    fun overrideHelperNextValue(newValue: String) {
        viewModelScope.launch {
            currentHelperValue = newValue
            helperNextValue.emit(newValue)
        }
    }

    fun setSelectedPlan(plan: Plan){
        selectedPlan.value = plan
        clickResetCounter.value = Pair(0,0)
        totalTrials.value = 0
        //prepare constraint
        viewModelScope.launch {
            selectedPlanConstraint.value = repository.getPlanConstraint(plan)
            if(selectedPlanConstraint.value != null){
                countDown.value = selectedPlanConstraint.value!!.value
            }
            else {
                countDown.value = null
            }
        }
        //prepare Helper
        viewModelScope.launch {
            selectedPlanHelper.value = repository.getPlanHelper(plan)
            if(selectedPlanHelper.value == null) {
                sessionType.value = null
            }else{
                getHelperNextValue = setupHelper(selectedPlanHelper.value!!.type, selectedPlanHelper.value!!.value)
                if (getHelperNextValue != null) {
                    currentHelperValue = getHelperNextValue!!()
                    helperNextValue.emit(currentHelperValue)
                }
                sessionType.value = selectedPlanHelper.value!!.type
            }
        }
    }

    private fun setupHelper(type: String, value: String): (() -> String)? {
        when (type) {
            PlanHelper.distance -> {
                helperValueList = distanceScheme.getValues(value.toFloat()).map { it.toString() }
                return { distanceScheme.getStep(value.toFloat()).toString() }
            }
            PlanHelper.duration -> {
                helperValueList = durationScheme.getValues(value.toFloat()).map { it.toString() }
                return { durationScheme.getStep(value.toFloat()).toString() }
            }
            PlanHelper.discrimination -> {
                helperValueList = value.split(",").map { it.trim() }
                return { value.split(",").map { it.trim() }.random() }
            }
            else -> {
                helperValueList = emptyList()
                return null
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

    //SharedFlow to broadcast success when a Trial is finished
    private val _currentTrial: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val currentTrial: SharedFlow<Boolean> = _currentTrial
    suspend fun addTrial(success: Boolean) {
        val trial = Trial(sessionID = session.id, success = success)
        _currentTrial.emit(trial.success)
        repository.insertTrial(trial)

        if (currentHelperValue != null)
            repository.insertTrialCriterion(TrialCriterion(trialID = trial.id, criterion = currentHelperValue!!))
        if (getHelperNextValue != null) {
            currentHelperValue = getHelperNextValue!!()
            helperNextValue.emit(currentHelperValue)
        }

        totalTrials.value++
        var (click, reset) = clickResetCounter.value
        clickResetCounter.value = if(success) Pair(++click, reset) else Pair(click, ++reset)
        if(selectedPlanConstraint.value?.type == PlanConstraint.repetition)
            countDown.value = countDown.value!! - 1
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
            constraintTimer = object : CountDownTimer(countDown.value!!.toLong() * 1000, 1000) {
                override fun onTick(p0: Long) {
                    countDown.value = countDown.value!! - 1
                }
                override fun onFinish() {
                }
            }
            constraintTimer.start()
        }
    }
    //enable cancel of timer from fragment
    fun cleanup(){
        //TODO stop the timer when leaving training fragment
        //restart when going back to existing training
        if(::constraintTimer.isInitialized) constraintTimer.cancel()
    }

    private val commentedSession: MutableStateFlow<Session?> = MutableStateFlow(value = null)
    fun setCommentedSession(session: Session) {
        commentedSession.value = session
    }

    suspend fun updateCommentedSession() {
        repository.updateCommentInSession(commentedSession.value)
    }
}