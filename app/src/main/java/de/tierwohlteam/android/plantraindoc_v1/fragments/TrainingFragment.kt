package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.media.SoundPool
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.TrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.ClickerStatus
import de.tierwohlteam.android.plantraindoc_v1.models.blueToothTools.Feeder
import de.tierwohlteam.android.plantraindoc_v1.models.ipTools.PTDCam
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_PTDCAM_URL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_AUTO_CLICK
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_FEEDER
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.KEY_USE_PTDCAM
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.VIBRATION_LONG
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.VIBRATION_SHORT
import de.tierwohlteam.android.plantraindoc_v1.others.percentage
import de.tierwohlteam.android.plantraindoc_v1.others.prettyStringFloat
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.ToolsViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.training_fragment) {
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val toolsViewModel: ToolsViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: TrainingFragmentBinding? = null
    private val binding get() = _binding!!

    private var soundPool: SoundPool? = null
    private var soundId = 1
    private var tts: TextToSpeech? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        // Lock the screen orientation
        // remember to free in onDestroyView!!!!
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Clicker
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        soundId = soundPool!!.load(activity, R.raw.click_test, 10)

        //Prepare text to speech
        //TODO choose app language
        val appLanguage = Locale.getDefault().language
        //val sysLanguage = Resources.getSystem().configuration.locales.toLanguageTags()
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                //tts?.language = Locale(appLanguage)
                tts?.language = Locale.GERMAN
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Select the right interface
        lifecycleScope.launchWhenStarted {
            trainingViewModel.sessionType.collect {
                when (it) {
                    PlanHelper.distance, PlanHelper.discrimination  -> UISingleValueHelper()
                    PlanHelper.duration -> UITimerHelper()
                    else -> UINoHelper()
                }.makeBindings()
            }
        }

        //Result text field
        lifecycleScope.launchWhenStarted {
            trainingViewModel.clickResetCounter.collect {
                val (click, reset) = it
                val text = "$click / $reset (${percentage(click, reset)} % ${getString(R.string.click)})"
                binding.resultTextview.text = text
            }
        }
        // 3BClicker
        lifecycleScope.launchWhenStarted {
            toolsViewModel.externalClicker.collect { click ->
                when (click) {
                    ClickerStatus.CLICK -> binding.buttonClick.performClick()
                    ClickerStatus.RESET -> binding.buttonReset.performClick()
                    else -> { /* NO-OP */ }
                }

            }
        }

        //start timer if constraint is time
        trainingViewModel.sessionTimer()
        //Constraint text field
        lifecycleScope.launchWhenStarted {
            trainingViewModel.countDown.collect {
                if(it != null) {
                    if (it <= 0) stopTraining()
                    binding.tvConstraintHeader.text = getString(R.string.remaining)
                    val text = it.toString()
                    binding.tvConstraintCounter.text = text
                } else {
                    binding.tvConstraintHeader.text = ""
                    binding.tvConstraintCounter.text = ""
                }
            }
        }

        // start PTDCam if activated
        if (sharedPreferences.getBoolean(KEY_USE_PTDCAM, false) && toolsViewModel.ptdCam != null) {
            binding.mjpegPtdcam.visibility = View.VISIBLE
            toolsViewModel.startPTDCamWindow(
                window = binding.mjpegPtdcam,
            )
            toolsViewModel.startPTDCamRecording(context)
        } else {
            binding.mjpegPtdcam.visibility = View.INVISIBLE
        }
    }

    private fun stopTraining() {
        vibrate()
        findNavController().popBackStack()
        //TODO at the first call after installation this jumps back too far
    }
    private fun vibrate(duration: String = "long"){
        val milliseconds = when(duration){
            "short" -> VIBRATION_SHORT
            else -> VIBRATION_LONG
        }
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(milliseconds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        lifecycleScope.launch(Dispatchers.IO) {
            val recorder = toolsViewModel.stopPTDCamRecording(context, trainingViewModel.session.id)
            Log.d("PTDCAM", "stop recording $recorder")
        }

        trainingViewModel.cleanup()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

/*
 * Generate the helpers here
 * Use inheritance if possible
 */

    /*
     * No helper
     */
    open inner class UINoHelper() {
        fun makeBindings() {
            makeButtonClick()
            makeButtonReset()
            makeHelper()
            makeExternalSensors()
            makeFeeder()
        }

        open fun makeButtonClick() {
            binding.buttonClick.setOnClickListener {
                if (sharedPreferences.getBoolean("useClicker", true))
                    soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                lifecycleScope.launchWhenStarted {
                    trainingViewModel.addTrial(true)
                }
            }
        }

        //Reset
        open fun makeButtonReset() {
            binding.buttonReset.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    trainingViewModel.addTrial(false)
                }
            }
        }

        //Helper text empty
        open fun makeHelper() {
            binding.tvHelperHeader.text = ""
            binding.tvHelperInfo.text = ""
        }

        //add additional tools which are able to click
        // this might be a light gate or an external clicker (3B Clicker)
        open fun makeExternalSensors() {
            var cooperate: Boolean = false
            lifecycleScope.launchWhenStarted {
                toolsViewModel.cooperationLightGate.collectLatest { cooperation ->
                    if (cooperation) {
                        tts!!.speak("Start", TextToSpeech.QUEUE_FLUSH, null, "")
                        cooperate = true
                    } else {
                        if (cooperate) {
                            tts!!.speak("Stop", TextToSpeech.QUEUE_FLUSH, null, "")
                            //trainingViewModel.addTrial(false) // Don't let this trigger the next collection
                            binding.buttonReset.performClick()
                            cooperate = false
                        }
                    }
                }
            }

            lifecycleScope.launchWhenStarted {
                trainingViewModel.currentTrial.collect {
                    cooperate = false
                }
            }
  /*          // 3BClicker
            lifecycleScope.launchWhenStarted {
                toolsViewModel.externalClicker.collect { click ->
                    when (click) {
                        ClickerStatus.CLICK -> binding.buttonClick.performClick()
                        ClickerStatus.RESET -> binding.buttonReset.performClick()
                        else -> { /* NO-OP */ }
                    }

                }
            } */
        }

        open fun makeFeeder() {
            viewLifecycleOwner.lifecycleScope.launch {
                trainingViewModel.currentTrial.collect{
                    if (sharedPreferences.getBoolean(KEY_USE_FEEDER, false))
                        if (it) Feeder.treat()
                }
            }
        }
    }
    /*
     * Helper for a single value like Distance or Discrimination
     * shows a single count in the helper information text view
     */
    // TODO can I observe on changes to tvHelperInfo? And then speak the new value?
    // No need for implementation in classes
    // Not directly tvHelperInfo, but this value here
    // HelperInfo is updated in countdown
    open inner class UISingleValueHelper() : UINoHelper() {
        override fun makeHelper() {
            lifecycleScope.launchWhenStarted {
                trainingViewModel.helperNextValue.collect { nextHelper ->
                    if (trainingViewModel.sessionType.value == PlanHelper.discrimination) prepareHelperValuePicker(nextHelper)
                    if (nextHelper != null) {
                        val speak = nextHelper.prettyStringFloat()
                        binding.tvHelperInfo.text = speak
                        if(sharedPreferences.getBoolean("useSpeechforHelper", true))
                            launch {
                                delay(1000)
                                tts!!.speak(speak, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                }
            }
        }

        private fun prepareHelperValuePicker(showValue: String?) {
            binding.tvHelperInfo.visibility = View.GONE
            val values = trainingViewModel.helperValueList.toTypedArray()
            binding.npHelper.apply {
                wrapSelectorWheel = false
                displayedValues = values
                minValue = 0
                maxValue = values.lastIndex
                value = values.indexOf(showValue)
                if (value == -1) value = 0
                setOnValueChangedListener { numberPicker, oldVal, newVal ->
                    trainingViewModel.overrideHelperNextValue(values[newVal])
                }
            }
            binding.npHelper.visibility = View.VISIBLE
        }
    }

    /*
     * Helper Timer
     * Start a timer with the click button
     * When timer is running, use Click button as standard
     */
    open inner class UITimerHelper() : UINoHelper(){
        lateinit var timer: CountDownTimer
        var timerIsRunning : Boolean = false

        override fun makeButtonClick() {
            binding.buttonClick.setBackgroundColor(resources.getColor(R.color.primaryColor))
            binding.buttonClick.text = getString(R.string.startTimer)
            binding.buttonClick.setOnClickListener {
                if (!timerIsRunning) {
                    timer.start()
                    timerIsRunning = true
                    binding.buttonClick.setBackgroundColor(resources.getColor(R.color.accent))
                    binding.buttonClick.text = getString(R.string.click)
                } else {
                    if(sharedPreferences.getBoolean("useClicker", true))
                        soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                    timer.cancel()
                    timerIsRunning = false
                    lifecycleScope.launchWhenStarted {
                        trainingViewModel.addTrial(true)
                    }
                    binding.buttonClick.setBackgroundColor(resources.getColor(R.color.primaryColor))
                    binding.buttonClick.text = getString(R.string.startTimer)
                }
            }
        }

        override fun makeButtonReset() {
            binding.buttonReset.setOnClickListener {
                if(timerIsRunning){
                    timer.cancel()
                    binding.buttonClick.setBackgroundColor(resources.getColor(R.color.primaryColor))
                    binding.buttonClick.text = getString(R.string.startTimer)
                    timerIsRunning = false
                }
                lifecycleScope.launchWhenStarted {
                    trainingViewModel.addTrial(false)
                }
            }
        }

        override fun makeHelper() {
            lifecycleScope.launchWhenStarted {
                trainingViewModel.helperNextValue.collect {
                    if (sharedPreferences.getBoolean("useSpeechforHelper", true)) {
                        delay(1000)
                        tts!!.speak(it?.prettyStringFloat() ?: "", TextToSpeech.QUEUE_FLUSH, null, "")
                    }
                }
            }
            lifecycleScope.launchWhenStarted {
                trainingViewModel.helperNextValue.collect {
                    binding.tvHelperInfo.text = it ?: ""
                    if (it != null) {
                        timer = object : CountDownTimer((it.toFloat() * 1000).toLong(), 1000) {
                            override fun onTick(p0: Long) {
                                binding.tvHelperInfo.text = (p0 / 1000).toString()
                            }
                            override fun onFinish() {
                                if (sharedPreferences.getBoolean(KEY_USE_AUTO_CLICK, false)) {
                                    binding.buttonClick.performClick()
                                } else {
                                    vibrate("short")
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun makeExternalSensors() {
            // LightGate
            var cooperate: Boolean = false
            viewLifecycleOwner.lifecycleScope.launch {
                toolsViewModel.cooperationLightGate.collect { cooperation ->
                    if (cooperation && !cooperate) {
                        cooperate = true
                        if (!timerIsRunning) {
                            tts!!.speak("Start", TextToSpeech.QUEUE_FLUSH, null, "")
                            binding.buttonClick.performClick()
                        }
                    }
                    if (!cooperation && cooperate) {
                        cooperate = false
                        if (timerIsRunning) {
                            tts!!.speak("Stop", TextToSpeech.QUEUE_FLUSH, null, "")
                            binding.buttonReset.performClick()
                        }
                    }
                }
            }
        }
    }
}

