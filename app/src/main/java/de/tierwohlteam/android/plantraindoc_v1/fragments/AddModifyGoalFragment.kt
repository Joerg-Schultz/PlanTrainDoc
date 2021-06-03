package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.AddModifyGoalFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.AddModifyGoalViewModel

@AndroidEntryPoint
class AddModifyGoalFragment : Fragment(R.layout.add_modify_goal_fragment) {
    private val viewModel: AddModifyGoalViewModel by viewModels()
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

        if (viewModel.goal != null) fillFields(viewModel.goal!!)

        binding.buttonSavegoal.setOnClickListener {
            //TODO Add Observer for insert Status
            // https://github.com/philipplackner/ShoppingListTestingYT/blob/TestItemDeletion/app/src/main/java/com/androiddevs/shoppinglisttestingyt/ui/AddShoppingItemFragment.kt
            viewModel.saveGoal(goalText = binding.tiGoal.text.toString(),
                description = binding.tiDescription.text.toString(),
                status = selectedStatus()
            )
        }

        binding.buttonCancel.setOnClickListener {
            //TODO move back
            view.findNavController().popBackStack()
        }
    }

    private fun subscribeToObservers() {
        viewModel.insertGoalStatus.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.ERROR -> {
                        Snackbar.make(binding.root,
                            result.message ?: "An unknown error occurred",
                            Snackbar.LENGTH_LONG
                        ).setAnchorView(R.id.button_savegoal)
                            .show()
                    }
                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Added Goal Item",
                            Snackbar.LENGTH_LONG
                        ).show()
                        findNavController().popBackStack()
                    }
                    else -> {}
                }
            }
        })
    }

    private fun selectedStatus() : String? {
        return when(binding.radioGroupStatus.checkedRadioButtonId) {
            R.id.radioButton_new -> Goal.statusNew
            R.id.radioButton_inprogress -> Goal.statusInProgress
            R.id.radioButton_stopped -> Goal.statusStopped
            R.id.radioButton_done -> Goal.statusFinished
            else -> null
        }
    }
    private fun fillFields(goal:Goal){
        binding.tiGoal.setText(goal.goal)
        binding.tiDescription.setText(goal.description)
        when(goal.status){
            Goal.statusNew -> binding.radioButtonNew.isChecked
            Goal.statusInProgress -> binding.radioButtonInprogress.isChecked
            Goal.statusStopped -> binding.radioButtonStopped.isChecked
            Goal.statusFinished -> binding.radioButtonDone.isChecked
        }
    }
}