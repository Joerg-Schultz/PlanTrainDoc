package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoaltreeFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.ShowTrainingFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.PlanConstraint
import de.tierwohlteam.android.plantraindoc_v1.models.PlanHelper
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.PlanViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.TrainingViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ShowTrainingFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val goal = goalViewModel.selectedGoal.value?.goal
        val plan = goalViewModel.selectedGoal.value?.plan
        binding.tvGoalForTraining.text = goal?.goal

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
            binding.tvPlanHelper.text =
                StringBuilder()
                .append(planHelper?.type ?: "")
                .append(planHelper?.value ?: "")
            binding.tvPlanConstraint.text =
                StringBuilder()
                    .append(planConstraint?.type ?: "")
                    .append(planConstraint?.value ?: "")
        }

    }
}