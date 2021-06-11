package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.SessionListAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.ShowTrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.*
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PlanViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ShowTrainingFragment : Fragment(R.layout.show_training_fragment) {
    private val goalViewModel: GoalViewModel by activityViewModels()
    private val planViewModel: PlanViewModel by activityViewModels()
    private val trainingViewModel: TrainingViewModel by activityViewModels()

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

    @OptIn(InternalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goal = goalViewModel.selectedGoal.value?.goal
        plan = goalViewModel.selectedGoal.value?.plan
        binding.tvGoalForTraining.text = goal?.goal
        setupConstraintAndHelperInfo()
        setupRecyclerView()
        lifecycleScope.launchWhenStarted {
            trainingViewModel.sessionWithRelationsList(goalViewModel.selectedGoal).collect {
                sessionListAdapter.submitList(it)
            }
        }

    }

    private fun setupRecyclerView() {
        binding.rvSessionlist.apply {
            sessionListAdapter = SessionListAdapter()
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
            // TODO overwrite PlanHelper / PlanConstraint toString method
            binding.tvPlanHelper.text = planConstraint?.let {translateAndFormatConstraint(it) }
            binding.tvPlanConstraint.text =
                StringBuilder()
                    .append(planConstraint?.type ?: "")
                    .append(planConstraint?.value ?: "")
        }

    }

    private fun translateAndFormatConstraint(constraint: PlanConstraint) : String {
        return when (constraint.type) {
            PlanConstraint.time -> "${constraint.value} ${getString(R.string.seconds)}"
            PlanConstraint.repetition -> "${constraint.value} ${getString(R.string.repetition)}"
            PlanConstraint.open -> getString(R.string.no_constraint)
            else -> ""
        }
    }
}