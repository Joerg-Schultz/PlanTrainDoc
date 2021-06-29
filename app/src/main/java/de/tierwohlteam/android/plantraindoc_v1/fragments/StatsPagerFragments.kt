package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SubGoalListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalClicksBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsSubGoalsBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsTimeCourseBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.others.LineChartMarkerView
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
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
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsGoalClicksBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

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
        goal = goalViewModel.selectedGoal.value?.goal
        plan = goalViewModel.selectedGoal.value?.plan
        if (plan == null){
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                clicksBarChart.visibility = View.GONE
            }
        } else {// else -> show bar chart
            trainingViewModel.setSelectedPlan(plan!!)
            binding.apply {
                tvNoPlan.visibility = View.GONE
                clicksBarChart.visibility = View.VISIBLE
            }
           lifecycleScope.launchWhenStarted {
                goalViewModel.subGoalsRecursive.collect {
                    if (it != null && it.isNotEmpty()) {
                        statisticsViewModel.analyzeGoals(it, level = "top")
                    }
                }
           }
         /*   lifecycleScope.launchWhenStarted {
                trainingViewModel.sessionWithRelationsList.collect {
                    statisticsViewModel.analyzeSessionList(it)
                }
            } */
            setupBarChart()
            lifecycleScope.launchWhenStarted {
                statisticsViewModel.clickResetCounter.collect {
                    if (it != null) {
                        val barDataSet = BarDataSet(
                            listOf(
                                BarEntry(1F, it.first.toFloat()),
                                BarEntry(2F, it.second.toFloat())
                            ), "ClickRatio"
                        )
                        barDataSet.setColors(
                            resources.getColor(R.color.accent),
                            resources.getColor(R.color.primaryLightColor)
                        )
                        binding.clicksBarChart.apply {
                            data = BarData(barDataSet)
                            invalidate()
                        }
                    }
                }
            }
        }
    }

    private fun setupBarChart(){
        binding.clicksBarChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
        }
        binding.clicksBarChart.axisLeft.apply {
            axisMinimum = 0F

        }
        binding.clicksBarChart.apply{
            description.text = getString(R.string.clickBarChart)
            legend.isEnabled = false
        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class TimeCourseFragment(title: String) : TabLayoutFragments(title = title) {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsTimeCourseBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsTimeCourseBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goal = goalViewModel.selectedGoal.value?.goal
        plan = goalViewModel.selectedGoal.value?.plan
        if (plan == null) {
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                timeCourseChart.visibility = View.GONE
            }
        } else {// else -> show bar chart
            trainingViewModel.setSelectedPlan(plan!!)
            binding.apply {
                tvNoPlan.visibility = View.GONE
                timeCourseChart.visibility = View.VISIBLE
            }
            lifecycleScope.launchWhenStarted {
                goalViewModel.subGoalsRecursive.collect {
                    if (it != null) {
                        statisticsViewModel.analyzeGoals(it, level = "top")
                    }
                }
            }
         /*   lifecycleScope.launchWhenStarted {
                trainingViewModel.sessionWithRelationsList.collect {
                    statisticsViewModel.analyzeSessionList(it)
                }
            } */
            setupLineChart()
            lifecycleScope.launchWhenStarted {
                statisticsViewModel.trialsFromPlan.collect {
                    if (it.isNotEmpty()) {
                        val dataList: MutableList<Entry> = mutableListOf()
                        for(chartPoint in it){
                            dataList.add(Entry(chartPoint.xValue.toFloat(),chartPoint.yValue.toFloat()))
                        }
                        val dataSet = LineDataSet(dataList,"Time Course")
                        binding.timeCourseChart.apply {
                            data = LineData(dataSet)
                            marker = LineChartMarkerView(it,requireContext(),R.layout.line_chart_annotation)
                            invalidate()
                        }
                    }
                }
            }
        }
    }
    private fun setupLineChart(){
        binding.timeCourseChart.xAxis.apply {
            axisMinimum = 0F
        }
        binding.timeCourseChart.axisLeft.apply {
            axisMinimum = 0F
        }
    }
}