package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.AddPlanFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AddPlanFragment : Fragment() {
    private val viewModel: GoalViewModel by activityViewModels()
    private var _binding: AddPlanFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = AddPlanFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        binding.tvGoalforplan.text = viewModel.selectedGoal.value?.goal ?: "No goal defined"
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
                        ).setAnchorView(R.id.button_savegoal)
                            .show()
                    }
                    Status.SUCCESS -> {
                        Snackbar.make(
                            binding.root,
                            "Added Training Plan",
                            Snackbar.LENGTH_LONG
                        ).show()
                        viewModel.setSelectedGoal(null)
                        findNavController().popBackStack()
                    }
                    else -> {
                    }
                }
            }
        })
    }

}