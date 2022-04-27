package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SubGoalListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalClicksBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalValuesBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsSubGoalsBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsTimeCourseBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsVideoBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.Plan
import de.tierwohlteam.android.plantraindoc_v1.others.LineChartMarkerView
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

enum class StatisticLevel {
    GOAL,
    GOALRECURSIVE,
    SESSION
}
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class SubGoalsFragment : Fragment() {

    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

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
            statisticsViewModel.goalList.collect {
                goalListAdapter.submitList(it)
            }
        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class ClicksFragment : Fragment() {
    companion object {
        fun newInstance(level: StatisticLevel): ClicksFragment {
            val args = Bundle()
            args.putSerializable("level", level)
            val fragment = ClicksFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsGoalClicksBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    private lateinit var level: StatisticLevel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.get("level") as StatisticLevel
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
        if (plan == null && level == StatisticLevel.GOAL){
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                clicksBarChart.visibility = View.GONE
                pBBarchart.visibility = View.GONE
            }
        } else {
            binding.apply {
                tvNoPlan.visibility = View.GONE
                clicksBarChart.visibility = View.VISIBLE
                pBBarchart.visibility = View.VISIBLE
            }
            setupBarChart()
            val counter = when (level) {
                StatisticLevel.GOAL -> statisticsViewModel.clickResetCounterGoal
                StatisticLevel.GOALRECURSIVE -> statisticsViewModel.clickResetCounterGoalRecursive
                StatisticLevel.SESSION -> statisticsViewModel.clickResetCounterSession
            }
            lifecycleScope.launchWhenStarted {
                counter.collect { result ->
                    when (result.status) {
                        Status.LOADING -> {
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
            granularity = 1F
            isGranularityEnabled = true
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
        fun newInstance(level: StatisticLevel): ValuesFragment {
            val args = Bundle()
            args.putSerializable("level", level)
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

    private lateinit var level: StatisticLevel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.get("level") as StatisticLevel
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

        if (plan == null) {
            binding.apply {
                tvNoPlan.visibility = View.VISIBLE
                valuesBarChart.visibility = View.GONE
                pBBarchart.visibility = View.GONE
            }
            return
        }
        // there is a plan
        //statisticsViewModel.analyzePlan(plan!!)

        binding.apply {
            tvNoPlan.visibility = View.GONE
            valuesBarChart.visibility = View.VISIBLE
            pBBarchart.visibility = View.VISIBLE
        }
        val counter = when (level) {
            StatisticLevel.SESSION -> statisticsViewModel.discreteValuesCounterSessions
            StatisticLevel.GOAL -> statisticsViewModel.discreteValuesCounterGoal
            StatisticLevel.GOALRECURSIVE -> throw Exception("Value Statistics not defined on recursive goals")
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            counter.collect { result ->
                when (result.status) {
                    Status.LOADING ->{
                        binding.pBBarchart.visibility = View.VISIBLE
                    }
                    Status.SUCCESS -> {
                        binding.pBBarchart.visibility = View.GONE
                        binding.valuesBarChart.setNoDataText(getString(R.string.no_data))
                       if (! result.data.isNullOrEmpty() ) {
                            val sortedResults = result.data.toSortedMap()
                            setupBarChart(sortedResults.keys.toList())
                            var xPos = 1
                            val plotDataList = mutableListOf<IBarDataSet>()
                            for ((criterion, counts) in sortedResults) {
                                val entry = BarEntry(xPos.toFloat(), floatArrayOf(counts.first.toFloat(), counts.second.toFloat()))
                                val dataset = BarDataSet(arrayListOf(entry), criterion)
                                dataset.apply {
                                    setColors(
                                        resources.getColor(R.color.accent),
                                        resources.getColor(R.color.primaryLightColor)
                                    )
                                    valueTextSize = 16F
                                    valueFormatter = DefaultValueFormatter(0)
                                }
                                plotDataList.add(dataset)
                                xPos++
                            }
                            binding.valuesBarChart.apply {
                                data = BarData(plotDataList)
                            }
                       } else {
                           setupBarChart(emptyList())
                       }
                        binding.valuesBarChart.invalidate()
                    }
                    else -> { /* NO-OP */ }
                }
            }
        }
    }

    private fun setupBarChart(bars: List<String>) {
        val barsPlusOffset = mutableListOf<String>("")
        barsPlusOffset.addAll(bars)
        binding.valuesBarChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            //isEnabled = false
            //labelCount = 2
            textSize = 16F
            labelRotationAngle = 45.0F
            valueFormatter = IndexAxisValueFormatter(barsPlusOffset)
        }
        binding.valuesBarChart.axisLeft.apply {
            axisMinimum = 0F
            granularity = 1F
            isGranularityEnabled = true
            setDrawGridLines(false)
        }
        binding.valuesBarChart.axisRight.apply {
            isEnabled = false
        }
        val click = LegendEntry()
        click.apply {
            label = getString(R.string.click)
            formColor = resources.getColor(R.color.accent)
            form = Legend.LegendForm.SQUARE
        }
        val reset = LegendEntry()
        reset.apply {
            label = getString(R.string.reset)
            formColor = resources.getColor(R.color.primaryLightColor)
            form = Legend.LegendForm.SQUARE
        }
        binding.valuesBarChart.apply{
            legend.setCustom(arrayOf(click, reset))
            description.isEnabled = false
            extraBottomOffset = 16F
        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class TimeCourseFragment : Fragment() {
    companion object {
        fun newInstance(level: StatisticLevel): TimeCourseFragment {
            val args = Bundle()
            args.putSerializable("level", level)
            val fragment = TimeCourseFragment()
            fragment.arguments = args
            return fragment
        }
    }
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsTimeCourseBinding? = null
    private val binding get() = _binding!!

    private var goal: Goal? = null
    private var plan: Plan? = null

    private lateinit var level: StatisticLevel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        level = arguments?.get("level") as StatisticLevel
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
        if (plan == null && level == StatisticLevel.GOAL) {
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
            setupLineChart()
            val chartPoints = if (level == StatisticLevel.GOAL) statisticsViewModel.chartPointsTop else statisticsViewModel.chartPoints
            lifecycleScope.launchWhenStarted {
                chartPoints.collect { result ->
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
                                val dataList: MutableList<Entry> = mutableListOf(Entry(0F,0F))
                                for (chartPoint in result.data) {
                                    dataList.add(Entry(chartPoint.xValue.toFloat() + 1, chartPoint.yValue.toFloat()))
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
            granularity = 1F
            isGranularityEnabled = true
        }
        binding.timeCourseChart.axisLeft.apply {
            axisMinimum = 0F
            granularity = 1F
            isGranularityEnabled = true
        }
        binding.timeCourseChart.axisRight.apply {
            axisMinimum = 0F
            granularity = 1F
            isGranularityEnabled = true
        }
        binding.timeCourseChart.apply{
            legend.isEnabled = false
            description.isEnabled = false
            extraBottomOffset = 16F
        }
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class VideoFragment : Fragment() {

    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: StatsVideoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = StatsVideoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val session = statisticsViewModel.sessionList.value.first() //Check that there is only one session?
        val sessionVideo = "${session.id}.mp4"
        val videoFiles = context?.getExternalFilesDir(null)!!.listFiles()?.toList() ?: emptyList()
        val videoFile = videoFiles.firstOrNull() { it.name == sessionVideo }
        if (videoFile != null) {
            binding.tvNosessionvideo.visibility = View.INVISIBLE
            val mediaController = MediaController(context)
            binding.videoView.apply {
                visibility = View.VISIBLE
                setMediaController(mediaController)
                setVideoPath(videoFile.path)
                requestFocus()
                start()
            }
        } else {
            binding.tvNosessionvideo.visibility = View.VISIBLE
            binding.videoView.visibility = View.INVISIBLE
        }
    }
}