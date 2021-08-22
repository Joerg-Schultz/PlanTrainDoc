package de.tierwohlteam.android.plantraindoc.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc.R
import de.tierwohlteam.android.plantraindoc.databinding.AddModifyGoalFragmentBinding
import de.tierwohlteam.android.plantraindoc.models.Goal
import de.tierwohlteam.android.plantraindoc.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc.others.Constants
import de.tierwohlteam.android.plantraindoc.others.Status
import de.tierwohlteam.android.plantraindoc.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddModifyGoalFragment : Fragment(R.layout.add_modify_goal_fragment) {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

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
            if(sharedPreferences.getBoolean(Constants.FIRST_GOAL, true)) {
                sharedPreferences.edit().putBoolean(Constants.FIRST_GOAL, true).apply()
            }
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