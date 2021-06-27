package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SubGoalListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalClicksBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsSubGoalsBinding
import de.tierwohlteam.android.plantraindoc_v1.models.GoalTreeItem
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
abstract class TabLayoutFragments(val title: String) : Fragment(){
}
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class SubGoalsFragment(title: String) : TabLayoutFragments(title = title) {
    private val goalViewModel: GoalViewModel by activityViewModels()

    private var _binding: StatsSubGoalsBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalListAdapter: SubGoalListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsSubGoalsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvStatssubgoals.apply {
            goalListAdapter = SubGoalListAdapter()
            adapter = goalListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        lifecycleScope.launchWhenStarted {
            goalViewModel.subGoalsRecursive.collect{
                if (it != null) {
                    goalListAdapter.submitList(it)
                }
            }
        }
    }
}
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class ClicksFragment(title: String) : TabLayoutFragments(title) {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsGoalClicksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsGoalClicksBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
