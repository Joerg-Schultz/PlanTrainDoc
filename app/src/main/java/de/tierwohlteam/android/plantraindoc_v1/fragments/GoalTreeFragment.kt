package de.tierwohlteam.android.plantraindoc_v1.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.adapters.GoalTreeAdapter
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoaltreeFragmentBinding
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.FIRST_GOAL
import de.tierwohlteam.android.plantraindoc_v1.others.Constants.FIRST_USAGE
import de.tierwohlteam.android.plantraindoc_v1.others.Status
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GoalTreeFragment : Fragment(R.layout.goaltree_fragment) {

    private val goalViewModel: GoalViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    private var _binding: GoaltreeFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var goalTreeAdapter: GoalTreeAdapter

    private var currentGoals : MutableList<GoalWithPlan> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = GoaltreeFragmentBinding.inflate(inflater, container, false)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // TODO how can I leave the app?
            // implement like in BoxBreathing If pressed twice then leave?
            goalViewModel.moveTreeUp()
        }
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        lifecycleScope.launchWhenStarted {
            goalViewModel.goals.collect { result ->
                when (result.status) {
                    Status.LOADING -> {
                        binding.pBgoaltree.visibility = View.VISIBLE

                    }
                    Status.SUCCESS -> {
                        binding.pBgoaltree.visibility = View.GONE
                        result.data?.let {
                            goalTreeAdapter.submitList(it)
                            if (it.isNotEmpty()) {
                                currentGoals = it as MutableList<GoalWithPlan>
                            } else {
                                when {
                                    firstUse("app") -> MaterialAlertDialogBuilder(view.context)
                                        .setTitle(getString(R.string.welcome))
                                        .setMessage(getString(R.string.welcome_message))
                                        .setPositiveButton(getString(R.string.ok)) { dialog, which -> {} }
                                        .show()
                                    //set FIRSTUSAGE to false
                                    firstUse("goal") -> MaterialAlertDialogBuilder(view.context)
                                        .setTitle(getString(R.string.congratulation))
                                        .setMessage(getString(R.string.firstgoal_message))
                                        .setPositiveButton(getString(R.string.ok)) { dialog, which -> {} }
                                        .show()
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
        binding.fabAddGoal.setOnClickListener {
            goalViewModel.setSelectedGoal(null)
            findNavController().navigate(R.id.action_goalTreeFragment_to_addModifyGoalFragment)
        }
    }

    private fun firstUse(level: String): Boolean {
        val selectedLevel = when(level){
            "app" -> FIRST_USAGE
            "goal" -> FIRST_GOAL
            else -> return false
        }
        return sharedPreferences.getBoolean(selectedLevel, true)
    }

    private fun setupRecyclerView() {
        binding.rvGoalTree.apply {
            goalTreeAdapter = GoalTreeAdapter { goal: GoalWithPlan -> goalViewModel.setSelectedGoal(goal) }
            adapter = goalTreeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.rvGoalTree)
    }

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        //0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            Collections.swap(currentGoals, fromPos, toPos)
            val titleList = currentGoals.map { it.goal.goal }
            recyclerView.adapter?.notifyItemMoved(fromPos,toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            lifecycleScope.launchWhenStarted {
                goalViewModel.updatePositions(currentGoals)
            }
            when(direction){
                ItemTouchHelper.LEFT -> goalViewModel.moveTreeDown(pos)
                ItemTouchHelper.RIGHT -> goalViewModel.moveTreeUp() //TODO allow only if there is a parent. Goals are not shown
            }
        }

    }

    override fun onDestroyView() {
        GlobalScope.launch {
            goalViewModel.updatePositions(currentGoals)
        }
        super.onDestroyView()
    }
}