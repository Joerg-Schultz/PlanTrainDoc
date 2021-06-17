package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.media.SoundPool
import android.os.Bundle
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
import de.tierwohlteam.android.plantraindoc_v1.others.percentage
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.training_fragment) {
    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var _binding: TrainingFragmentBinding? = null
    private val binding get() = _binding!!

    private var soundPool: SoundPool? = null
    private var soundId = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        soundId = soundPool!!.load(activity, R.raw.click_test, 10)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            stopTraining(view)
        }
        return view
    }

    private fun stopTraining(view: View) {
        trainingViewModel.cleanup()
        view.findNavController().popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Select the right interface
        lifecycleScope.launchWhenStarted {
            trainingViewModel.sessionType.collect {
                createClickResetHelperText(it)
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

    private fun createClickResetHelperText(sessionType: String?) {
        val ui = when (sessionType) {
            PlanHelper.distance -> UIDistanceHelper()
            PlanHelper.duration -> UIDistanceHelper()
            else -> UINoHelper()
        }
        ui.makeBindings()
    }

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

    open inner class UIDistanceHelper() : UINoHelper() {
        override fun makeHelper() {
            lifecycleScope.launchWhenStarted {
                trainingViewModel.helperNextValue.collect {
                    binding.tvHelperInfo.text = it
                }
            }
        }
    }
}
