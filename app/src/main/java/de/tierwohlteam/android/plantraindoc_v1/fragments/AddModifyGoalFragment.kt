package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.AddModifyGoalFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddModifyGoalFragment : Fragment(R.layout.add_modify_goal_fragment) {
    private val viewModel: GoalViewModel by activityViewModels()
    private var _binding: AddModifyGoalFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddModifyGoalFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        viewModel.selectedGoal.value?.let { fillFields(it) }

        binding.buttonDeletegoal.visibility = if (viewModel.selectedGoal.value == null) INVISIBLE else VISIBLE
        binding.buttonDeletegoal.setOnClickListener {
            viewModel.deleteGoal()
        }

        binding.fabSavegoal.setOnClickListener {
            viewModel.saveNewOrUpdatedGoal(
                goalText = binding.tiGoal.text.toString(),
                description = binding.tiDescription.text.toString(),
                status = selectedStatus(),
                goalWP = viewModel.selectedGoal.value
            )
        }
            /*
            binding.buttonSavegoal.setOnClickListener {
                 viewModel.saveNewOrUpdatedGoal(goalText = binding.tiGoal.text.toString(),
                    description = binding.tiDescription.text.toString(),
                    status = selectedStatus(),
                     goalWP = viewModel.selectedGoal.value
                )
            }

            binding.buttonCancel.setOnClickListener {
                viewModel.setSelectedGoal(null)
                view.findNavController().popBackStack()
            } */
    }

    private fun subscribeToObservers() {
        //Did the insert work?
        viewModel.insertGoalStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> {
                        Snackbar.make(
                            binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.fab_savegoal)
                            .show()
                    }
                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Added/Deleted Goal Item",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.fab_savegoal)
                            .show()
                        viewModel.setSelectedGoal(null)
                        findNavController().popBackStack()
                    }
                    else -> {
                    }
                }
            }
        })
    }

    private fun selectedStatus(): String? {
        return when (binding.radioGroupStatus.checkedRadioButtonId) {
            R.id.radioButton_new -> Goal.statusNew
            R.id.radioButton_inprogress -> Goal.statusInProgress
            R.id.radioButton_stopped -> Goal.statusStopped
            R.id.radioButton_done -> Goal.statusFinished
            else -> null
        }
    }

    private fun fillFields(goalWithPlan: GoalWithPlan) {
        val goal = goalWithPlan.goal
        binding.tiGoal.setText(goal.goal)
        binding.tiDescription.setText(goal.description)
        when (goal.status) {
            Goal.statusNew -> binding.radioButtonNew.isChecked = true
            Goal.statusInProgress -> binding.radioButtonInprogress.isChecked = true
            Goal.statusStopped -> binding.radioButtonStopped.isChecked = true
            Goal.statusFinished -> binding.radioButtonDone.isChecked = true
        }
    }
}