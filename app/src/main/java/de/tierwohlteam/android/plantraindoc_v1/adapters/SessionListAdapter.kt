package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.GoalItemBinding
import de.tierwohlteam.android.plantraindoc_v1.databinding.SessionItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.GoalWithPlan
import de.tierwohlteam.android.plantraindoc_v1.models.Session

class SessionListAdapter : RecyclerView.Adapter<SessionListAdapter.SessionViewHolder>()  {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<Session>(){
        override fun areItemsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Session, newItem: Session): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Session>) = differ.submitList(list)

    inner class SessionViewHolder(val binding: SessionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionListAdapter.SessionViewHolder {
        val binding = SessionItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return SessionViewHolder(binding)
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SessionListAdapter.SessionViewHolder, position: Int) {
        val session = differ.currentList[position]
    }

}