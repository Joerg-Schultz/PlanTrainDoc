package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.R
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoalItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.models.Plan

class GoalTreeAdapter(private val selectGoal: (GoalWithPlan) -> Unit)
    : RecyclerView.Adapter<GoalTreeAdapter.GoalViewHolder>()  {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<GoalWithPlan>(){
        override fun areItemsTheSame(oldItem: GoalWithPlan, newItem: GoalWithPlan): Boolean {
            return oldItem.goal.id == newItem.goal.id
        }
        override fun areContentsTheSame(oldItem: GoalWithPlan, newItem: GoalWithPlan): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<GoalWithPlan>) = differ.submitList(list)

    inner class GoalViewHolder(val binding: GoalItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = GoalItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goalWithPlan = differ.currentList[position]
        holder.binding.apply {
            tvGoal.text = goalWithPlan.goal.goal
            tvDetails.text = goalWithPlan.goal.description
            tvStatus.text = goalWithPlan.goal.status
            if(goalWithPlan.plan == null) {
                btnAction.text = holder.itemView.context.getText(R.string.plan_verb)
                btnAction.setOnClickListener {
                    selectGoal(goalWithPlan)
                    it.findNavController().navigate(R.id.action_goalTreeFragment_to_addPlanFragment)
                }
            } else {
                btnAction.text = holder.itemView.context.getText(R.string.train)
                btnAction.setOnClickListener {
                    selectGoal(goalWithPlan)
                    it.findNavController().navigate(R.id.action_goalTreeFragment_to_showTrainingFragment)
                }
            }
        }
        holder.itemView.setOnClickListener { view ->
            selectGoal(goalWithPlan)
            view.findNavController().navigate(R.id.action_goalTreeFragment_to_addModifyGoalFragment)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
