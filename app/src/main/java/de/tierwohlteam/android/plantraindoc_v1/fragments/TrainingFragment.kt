package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.TrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.PlanWithRelations
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.training_fragment) {
    private val trainingViewModel: TrainingViewModel by activityViewModels()

    private var planWithRelations: PlanWithRelations? = null
    private var _binding: TrainingFragmentBinding? = null
    private val binding get() = _binding!!

    private var soundPool: SoundPool? = null
    private var soundId = 1

    // I need the plan with Relations here
    // I generate a new session object in on create view
    // I add trials

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        soundPool = SoundPool.Builder()
            .setMaxStreams(1)
            .build()
        //soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundId = soundPool!!.load(activity, R.raw.click_test, 10)
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            trainingViewModel.selectedPlanWithRelations.collect {
                planWithRelations = it
            }
        }
        binding.buttonClick.setOnClickListener {
            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
            lifecycleScope.launchWhenStarted {
                trainingViewModel.addTrial(true)
            }
        }

        binding.buttonReset.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                trainingViewModel.addTrial(false)
            }
        }
    }
}