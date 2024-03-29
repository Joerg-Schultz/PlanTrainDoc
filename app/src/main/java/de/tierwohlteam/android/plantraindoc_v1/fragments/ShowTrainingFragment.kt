package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SessionListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.ShowTrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PlanViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.StatisticsViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@AndroidEntryPoint
class ShowTrainingFragment : Fragment(R.layout.show_training_fragment) {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val planViewModel: PlanViewModel by activityViewModels()
    private val trainingViewModel: TrainingViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()

    private var _binding: ShowTrainingFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var sessionListAdapter: SessionListAdapter

    private var goal: Goal? = null
    private var plan: Plan? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ShowTrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goal = goalViewModel.selectedGoal.value?.goal
        plan = goalViewModel.selectedGoal.value?.plan
        binding.tvGoalForTraining.text = goal?.goal
        setupConstraintAndHelperInfo()
        setupRecyclerView()
        plan?.let { trainingViewModel.setSelectedPlan(it) }
        lifecycleScope.launchWhenStarted {
            trainingViewModel.sessionWithRelationsList.collect {
                sessionListAdapter.submitList(it.reversed())
            }
        }
        binding.btnTrain.setOnClickListener {
            val criterion: String = binding.tiCriterion.text.toString()
            lifecycleScope.launchWhenStarted {
                trainingViewModel.newSession(criterion)
            }
            findNavController().navigate(R.id.action_showTrainingFragment_to_trainingFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.rvSessionlist.apply {
            sessionListAdapter = SessionListAdapter(context, { session: Session ->
                trainingViewModel.setCommentedSession(
                    session
                )
            }, { session: Session -> statisticsViewModel.setSessionList(listOf(session)) } )
            adapter = sessionListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupConstraintAndHelperInfo() {
        lifecycleScope.launch {
            var planHelper: PlanHelper? = null
            val planHelperJob = launch {
                planHelper = plan?.let { planViewModel.getHelper(it) }
            }
            var planConstraint: PlanConstraint? = null
            val planConstraintJob = launch {
                planConstraint = plan?.let { planViewModel.getConstraint(it) }
            }
            planHelperJob.join()
            planConstraintJob.join()
            // TODO Use PlanWithRelations instead ->just a single call
            binding.tvPlanConstraint.text = translateAndFormatConstraint(planConstraint)
            binding.tvPlanHelper.text = translateAndFormatHelper(planHelper)
        }
    }

    private fun translateAndFormatConstraint(constraint: PlanConstraint?) : String {
        return if(constraint == null) getString(R.string.no_constraint) else
        when (constraint.type) {
            PlanConstraint.time -> "${constraint.value} ${getString(R.string.seconds)}"
            PlanConstraint.repetition -> "${constraint.value} ${getString(R.string.repetition)}"
            PlanConstraint.open -> getString(R.string.no_constraint)
            else -> ""
        }
    }

    private fun translateAndFormatHelper(helper: PlanHelper?) : String{
        return if(helper == null) getString(R.string.no_helper) else
        when (helper.type){
            PlanHelper.duration -> "${getString(R.string.duration)} ${helper.value} ${getString(R.string.seconds)}"
            PlanHelper.distance -> "${getString(R.string.distance)} ${helper.value}"
            PlanHelper.cueIntroduction -> "${getString(R.string.cue_introduction)} ${helper.value} %"
            PlanHelper.discrimination -> "${getString(R.string.discrimination)} ${helper.value}"
            PlanHelper.free -> getString(R.string.no_helper)
            else -> ""
        }
    }

    override fun onPause() {
        GlobalScope.launch {
            trainingViewModel.updateCommentedSession()
        }
        super.onPause()
    }
 /*   override fun onDestroyView() {
        GlobalScope.launch {
            trainingViewModel.updateCommentedSession()
        }
        super.onDestroyView()
    } */
}