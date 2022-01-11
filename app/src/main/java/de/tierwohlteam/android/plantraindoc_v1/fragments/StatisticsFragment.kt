package de.tierwohlteam.android.plantraindoc_v1.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.StatsViewPagerAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.ShowTrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatisticsFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PlanViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.statistics_fragment) {

    private val goalViewModel: GoalViewModel by activityViewModels()
    private val planViewModel: PlanViewModel by activityViewModels()

    private var _binding: StatisticsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = StatisticsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val goal = goalViewModel.selectedGoal.value?.goal ?: return
        binding.statsGoal.text = goal.goal
        val viewPager2 = view.findViewById<ViewPager2>(R.id.stats_pager_container)

        val fragmentTitleList: Map<String,Fragment> = mapOf(
            getString(R.string.click) to ClicksFragment.newInstance(level = "top"),
            getString(R.string.values) to ValuesFragment.newInstance(level = "top"),
            getString(R.string.trend) to TimeCourseFragment.newInstance(level = "top"),
            getString(R.string.goals) to SubGoalsFragment(),
            getString(R.string.total_clicks) to ClicksFragment.newInstance(level = "all"),
            getString(R.string.total_trend) to TimeCourseFragment.newInstance(level = "all"),
        )

        viewPager2.adapter = StatsViewPagerAdapter(this.childFragmentManager, lifecycle,
            ArrayList(fragmentTitleList.values)
        )

        val tabLayout = view.findViewById<TabLayout>(R.id.stats_tablayout)
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = fragmentTitleList.keys.toTypedArray()[position]
        }.attach()

    }
}