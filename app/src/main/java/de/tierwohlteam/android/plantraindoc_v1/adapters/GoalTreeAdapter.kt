package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoalItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal

class GoalTreeAdapter: RecyclerView.Adapter<GoalTreeAdapter.GoalViewHolder>()  {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<Goal>(){
        override fun areItemsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Goal, newItem: Goal): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Goal>) = differ.submitList(list)

    inner class GoalViewHolder(val binding: GoalItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = GoalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = differ.currentList[position]
        val goalText = goal.goal
        holder.binding.apply {
            tvGoal.text = goalText
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
