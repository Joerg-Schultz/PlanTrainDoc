package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.Context
import android.content.res.Resources
import android.media.SoundPool
import android.os.*
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.TrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.VIBRATION_LONG
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.VIBRATION_SHORT
import de.tierwohlteam.android.plantraindoc_v1.others.percentage
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.training_fragment) {
    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var _binding: TrainingFragmentBinding? = null
    private val binding get() = _binding!!

    private var soundPool: SoundPool? = null
    private var soundId = 1
    private var tts: TextToSpeech? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        // Clicker
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        soundId = soundPool!!.load(activity, R.raw.click_test, 10)

        //Prepare text to speech
        val appLanguage = Locale.getDefault().language
        //val sysLanguage = Resources.getSystem().configuration.locales.toLanguageTags()
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                //tts?.language = Locale(appLanguage)
                tts?.language = Locale.GERMAN
            }
        }


        //Back button stops training
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            stopTraining(view)
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

        //start timer if constraint is time
        trainingViewModel.sessionTimer()
        //Constraint text field
        lifecycleScope.launchWhenStarted {
            trainingViewModel.countDown.collect {
                val text = it.toString()
                binding.tvConstraintCounter.text = text
            }
        }

        //stop when countdown is 0
        lifecycleScope.launchWhenStarted {
            trainingViewModel.countDown.collect {
                if (it != null && it <= 0) {
                    stopTraining(view)
                }
            }
        }
    }

    private fun stopTraining(view: View) {
        vibrate()
        trainingViewModel.cleanup()
        view.findNavController().popBackStack()
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
        }

        open fun makeButtonClick() {
            binding.buttonClick.setOnClickListener {
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
                trainingViewModel.helperNextValue.collect {
                    if (it != null) {
                        binding.tvHelperInfo.text = it
                        launch {
                            delay(1000)
                            tts!!.speak(it, TextToSpeech.QUEUE_FLUSH, null, "")
                        }
                    }
                }
            }
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
                    soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                    lifecycleScope.launchWhenStarted {
                        trainingViewModel.addTrial(true)
                    }
                    if(timerIsRunning){
                        timer.cancel()
                        timerIsRunning = false
                    }
                    binding.buttonClick.setBackgroundColor(resources.getColor(R.color.primaryColor))
                    binding.buttonClick.text = getString(R.string.startTimer)
                }
            }
        }

        override fun makeButtonReset() {
            binding.buttonReset.setOnClickListener {
                lifecycleScope.launchWhenStarted {
                    trainingViewModel.addTrial(false)
                }
                if(timerIsRunning){
                    timer.cancel()
                    binding.buttonClick.setBackgroundColor(resources.getColor(R.color.primaryColor))
                    binding.buttonClick.text = getString(R.string.startTimer)
                    timerIsRunning = false
                }
            }
        }

        override fun makeHelper() {
            lifecycleScope.launchWhenStarted {
                trainingViewModel.helperNextValue.collect {
                    binding.tvHelperInfo.text = it
                    if (it != null) {
                        launch {
                            delay(1000)
                            tts!!.speak(it, TextToSpeech.QUEUE_FLUSH, null,"")
                        }
                        timer = object : CountDownTimer((it.toFloat() * 1000).toLong(), 1000) {
                            override fun onTick(p0: Long) {
                                binding.tvHelperInfo.text = (p0 / 1000).toString()
                            }

                            override fun onFinish() {
                                vibrate("short")
                            }
                        }
                    }
                }
            }
        }
    }
}

