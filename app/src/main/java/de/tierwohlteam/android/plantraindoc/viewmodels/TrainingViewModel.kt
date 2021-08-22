package de.tierwohlteam.android.plantraindoc.viewmodels

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.plantraindoc.models.*
import de.tierwohlteam.android.plantraindoc.repositories.PTDRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

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

    // TODO use non Mutables for interaction with outside world
    var totalTrials : MutableStateFlow<Int> = MutableStateFlow(value = 0)
    var countDown : MutableStateFlow<Int?> = MutableStateFlow(value = null)
    var clickResetCounter: MutableStateFlow<Pair<Int,Int>> = MutableStateFlow(value = Pair(0,0))
    //var helperNextValue: MutableStateFlow<String?> = MutableStateFlow(value = null)
    var helperNextValue: MutableSharedFlow<String?> = MutableSharedFlow(replay = 1)
    var sessionType: MutableStateFlow<String?> = MutableStateFlow(value = null)

    private val selectedPlan: MutableStateFlow<Plan?> = MutableStateFlow(value = null)
    private val selectedPlanConstraint: MutableStateFlow<PlanConstraint?> = MutableStateFlow(value = null)
    private val selectedPlanHelper: MutableStateFlow<PlanHelper?> = MutableStateFlow(value = null)
    private var getHelperNextValue : (() -> String)? = null

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
                helperNextValue.emit(getHelperNextValue?.let { it() })
                sessionType.value = selectedPlanHelper.value!!.type
            }
        }
    }

    private fun setupHelper(type: String, value: String): (() -> String)? {
        return when (type) {
            PlanHelper.distance -> {
                { distanceScheme.getStep(value.toFloat()).toString() }
            }
            PlanHelper.duration -> {
                { durationScheme.getStep(value.toFloat()).toString() }
            }
            PlanHelper.discrimination -> {
                { value.split(",").random() }
            }
            else -> null
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
        //Using GlobalScope as the insert also has to happen when
        // training is stopped and ViewModel is closed
        GlobalScope.launch {
            repository.insertTrial(trial)
            helperNextValue.collect {
                if (it != null) {
                    val trialCriterion = TrialCriterion(trialID = trial.id, criterion = it)
                    repository.insertTrialCriterion(trialCriterion)
                }
            }
        }
        totalTrials.value++
        var (click, reset) = clickResetCounter.value
        clickResetCounter.value = if(success) Pair(++click, reset) else Pair(click, ++reset)
        if(selectedPlanConstraint.value?.type == PlanConstraint.repetition)
            countDown.value = countDown.value!! - 1
        helperNextValue.emit(getHelperNextValue?.let { it() })
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