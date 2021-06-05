package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoalItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.Goal
import de.tierwohlteam.android.plantraindoc_v1.viewmodels.GoalViewModel

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
        holder.binding.apply {
            tvGoal.text = goal.goal
            tvDetails.text = goal.description
            tvStatus.text = goal.status
            btnAction.text = "Add Training"
            btnSubgoals.setOnClickListener {
                //update viewModel.parentGoal

            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
