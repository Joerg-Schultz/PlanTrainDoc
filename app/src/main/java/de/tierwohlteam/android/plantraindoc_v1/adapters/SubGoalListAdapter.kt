package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalItemBinding

class SubGoalListAdapter(private val goalList: List<String>) : RecyclerView.Adapter<SubGoalListAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(val binding: StatsGoalItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalListAdapter.GoalViewHolder {
        val binding = StatsGoalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGoalListAdapter.GoalViewHolder, position: Int) {
        val goal = goalList[position]
        holder.binding.tvGoal.text = goal
    }

    override fun getItemCount(): Int {
        return goalList.size
    }

}