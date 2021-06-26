package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.StatsGoalItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.models.GoalTreeItem
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan

class SubGoalListAdapter() :
    RecyclerView.Adapter<SubGoalListAdapter.GoalViewHolder>() {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<GoalTreeItem>(){
        override fun areItemsTheSame(oldItem: GoalTreeItem, newItem: GoalTreeItem): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: GoalTreeItem, newItem: GoalTreeItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<GoalTreeItem>) = differ.submitList(list)


    inner class GoalViewHolder(val binding: StatsGoalItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubGoalListAdapter.GoalViewHolder {
        val binding = StatsGoalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubGoalListAdapter.GoalViewHolder, position: Int) {
        val goal = differ.currentList[position]
        holder.binding.tvGoal.text = goal.goal
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}