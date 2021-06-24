package de.tierwohlteam.android.plantraindoc_v1.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.ShowTrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatisticsFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.statistics_fragment) {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val statsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goal = goalViewModel.selectedGoal.value?.goal
        binding.tvStatsforgoal.text = goal?.goal

    }
}