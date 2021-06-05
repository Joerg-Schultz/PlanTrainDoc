package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.GoalTreeAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoaltreeFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalTreeViewModel
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GoalTreeFragment : Fragment(R.layout.goaltree_fragment) {

    private val viewModel: GoalViewModel by activityViewModels()

    private var _binding: GoaltreeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalTreeAdapter: GoalTreeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GoaltreeFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        lifecycleScope.launchWhenStarted {
            viewModel.goals.collect {
                //TODO show info message if there are no goals (it.isEmpty() )
                goalTreeAdapter.submitList(it)
            }
        }
        binding.fabAddGoal.setOnClickListener {
            findNavController().navigate(R.id.action_goalTreeFragment_to_addModifyPlanFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.rvGoalTree.apply {
            goalTreeAdapter = GoalTreeAdapter()
            adapter = goalTreeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvGoalTree)
    }

    val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        //ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            TODO("Not yet implemented")
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            when(direction){
                ItemTouchHelper.LEFT -> viewModel.moveTreeDown(pos)
                ItemTouchHelper.RIGHT -> viewModel.moveTreeUp()
            }
        }

    }
}