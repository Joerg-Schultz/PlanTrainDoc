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
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SubGoalListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalClicksBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalValuesBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsSubGoalsBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsTimeCourseBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.others.LineChartMarkerView
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class SubGoalsFragment : Fragment() {

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
class ClicksFragment : Fragment() {
    companion object {
        fun newInstance(level: String): ClicksFragment {
            val args = Bundle()
            args.putString("level", level)
            val fragment = ClicksFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsGoalClicksBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    private var level: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.getString("level") ?: "all"
    }
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
        if (plan == null && level == "top"){
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                clicksBarChart.visibility = View.GONE
                pBBarchart.visibility = View.GONE
            }
        } else {// else -> show bar chart
            //trainingViewModel.setSelectedPlan(plan!!)
            binding.apply {
                tvNoPlan.visibility = View.GONE
                clicksBarChart.visibility = View.VISIBLE
                pBBarchart.visibility = View.VISIBLE
            }
           lifecycleScope.launchWhenStarted {
                goalViewModel.subGoalsRecursive.collect {
                    if (it != null && it.isNotEmpty()) {
                        statisticsViewModel.analyzeGoals(it, level = level)
                    }
                }
           }
            setupBarChart()
            lifecycleScope.launchWhenStarted {
                statisticsViewModel.clickResetCounter.collect { result ->
                    when(result.status){
                        Status.LOADING ->{
                            binding.pBBarchart.visibility = View.VISIBLE
                        }
                        Status.SUCCESS -> {
                            if (result.data != null) {
                                binding.pBBarchart.visibility = View.GONE
                                val barDataSet = BarDataSet(
                                    listOf(
                                        BarEntry(1F, result.data.first.toFloat()),
                                        BarEntry(2F, result.data.second.toFloat())
                                    ), "ClickRatio"
                                )
                                barDataSet.apply {
                                    setColors(
                                        resources.getColor(R.color.accent),
                                        resources.getColor(R.color.primaryLightColor)
                                    )
                                    valueTextSize = 16F
                                    valueFormatter = DefaultValueFormatter(0)
                                }
                                binding.clicksBarChart.apply {
                                    data = BarData(barDataSet)
                                    setNoDataText("No Training for this goal")
                                    invalidate()

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupBarChart(){
        binding.clicksBarChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            //isEnabled = false
            labelCount = 2
            textSize = 16F
            valueFormatter = IndexAxisValueFormatter(listOf("","Click", "Reset"))
        }
        binding.clicksBarChart.axisLeft.apply {
            axisMinimum = 0F
            setDrawGridLines(false)
        }
        binding.clicksBarChart.axisRight.apply {
            isEnabled = false
        }
        binding.clicksBarChart.apply{
            legend.isEnabled = false
            description.isEnabled = false
            extraBottomOffset = 16F

        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class ValuesFragment : Fragment() {
    companion object {
        fun newInstance(level: String): ValuesFragment {
            val args = Bundle()
            args.putString("level", level)
            val fragment = ValuesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val goalViewModel: GoalViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsGoalValuesBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    private var level: String = "top" //This fragment makes only sense for a single goal / Plan

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.getString("level") ?: "top"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsGoalValuesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goal = goalViewModel.selectedGoal.value?.goal
        plan = goalViewModel.selectedGoal.value?.plan

        if (plan == null && level == "top") {
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                valuesBarChart.visibility = View.GONE
                pBBarchart.visibility = View.GONE
            }
            return
        }
        // there is a plan
        binding.apply {
            tvNoPlan.visibility = View.GONE
            valuesBarChart.visibility = View.VISIBLE
            pBBarchart.visibility = View.VISIBLE
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            statisticsViewModel.discreteValuesCounter.collect { result ->

            }
        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class TimeCourseFragment : Fragment() {
    companion object {
        fun newInstance(level: String): TimeCourseFragment {
            val args = Bundle()
            args.putString("level", level)
            val fragment = TimeCourseFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsTimeCourseBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    private var level: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.getString("level") ?: "all"
    }

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
        if (plan == null && level == "top") {
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                timeCourseChart.visibility = View.GONE
                pBTimecourse.visibility = View.GONE
            }
        } else {// else -> show bar chart
            //trainingViewModel.setSelectedPlan(plan!!)
            binding.apply {
                tvNoPlan.visibility = View.GONE
                timeCourseChart.visibility = View.VISIBLE
            }
            lifecycleScope.launchWhenStarted {
                goalViewModel.subGoalsRecursive.collect {
                    if (it != null) {
                        statisticsViewModel.analyzeGoals(it, level = level)
                    }
                }
            }
            setupLineChart()
            lifecycleScope.launchWhenStarted {
                statisticsViewModel.trialsFromPlan.collect { result ->
                    when (result.status) {
                        Status.LOADING -> {
                            binding.pBTimecourse.visibility = View.VISIBLE
                        }
                        Status.SUCCESS -> {
                            binding.pBTimecourse.visibility = View.GONE
                            binding.timeCourseChart.apply {
                                axisLeft.apply {
                                    setDrawGridLines(false)
                                }
                                setNoDataText(getString(R.string.no_training))
                            }
                            if (result.data!!.isNotEmpty()) {
                                val dataList: MutableList<Entry> = mutableListOf()
                                for (chartPoint in result.data) {
                                    dataList.add(Entry(chartPoint.xValue.toFloat(), chartPoint.yValue.toFloat()))
                                }
                                val dataSet = LineDataSet(dataList, "Time Course")
                                dataSet.circleRadius = 8f
                                binding.timeCourseChart.data = LineData(dataSet)
                                binding.timeCourseChart.data.setDrawValues(false)

                                binding.timeCourseChart.marker = LineChartMarkerView(
                                    result.data,
                                    requireContext(),
                                    R.layout.line_chart_annotation
                                )
                            }
                            binding.timeCourseChart.invalidate()
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
        binding.timeCourseChart.apply{
            legend.isEnabled = false
            description.isEnabled = false
            extraBottomOffset = 16F
        }
    }
}
