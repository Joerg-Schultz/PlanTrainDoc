package de.tierwohlteam.android.plantraindoc_v1.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.AddModifyGoalViewModel
import de.tierwohlteam.android.plantraindoc_v1.R

class AddModifyGoalFragment : Fragment() {

    companion object {
        fun newInstance() = AddModifyGoalFragment()
    }

    private lateinit var viewModel: AddModifyGoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_modify_goal_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddModifyGoalViewModel::class.java)
        // TODO: Use the ViewModel
    }

}