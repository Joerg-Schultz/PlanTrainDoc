package de.tierwohlteam.android.plantraindoc_v1.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc_v1.databinding.SessionItemBinding
import de.tierwohlteam.android.plantraindoc_v1.models.SessionWithRelations

class SessionListAdapter : RecyclerView.Adapter<SessionListAdapter.SessionViewHolder>()  {

    // generate a diff list to update only changed items in the RecView
    private val diffCallback = object : DiffUtil.ItemCallback<SessionWithRelations>(){
        override fun areItemsTheSame(oldItem: SessionWithRelations, newItem: SessionWithRelations): Boolean {
            return oldItem.session.id == newItem.session.id
        }
        override fun areContentsTheSame(oldItem: SessionWithRelations, newItem: SessionWithRelations): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<SessionWithRelations>) = differ.submitList(list)

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
        val session = differ.currentList[position].session
    }

}