package de.tierwohlteam.android.plantraindoc.adapters

import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import de.tierwohlteam.android.plantraindoc.R
import de.tierwohlteam.android.plantraindoc.databinding.SessionItemBinding
import de.tierwohlteam.android.plantraindoc.models.Session
import de.tierwohlteam.android.plantraindoc.models.SessionWithRelations
import de.tierwohlteam.android.plantraindoc.others.percentage

class SessionListAdapter(private val addComment: (Session) -> Unit) : RecyclerView.Adapter<SessionListAdapter.SessionViewHolder>()  {

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
        val trials = differ.currentList[position].trials
        val successes = trials.count { it.success }
        val resets = trials.count{ !it.success }
        val percent = percentage(successes,resets)
        val successText = holder.itemView.context.getText(R.string.success)
        val evaluationText = "$successText: $percent % ($successes / $resets)"
        if(position == 0){
            holder.binding.apply {
                tvComment.visibility = GONE
                tilComment.visibility = VISIBLE
                tiComment.visibility = VISIBLE
                if(session.comment != null) tiComment.setText(session.comment)
                tiComment.doAfterTextChanged {
                    session.comment = tiComment.text.toString()
                    addComment(session)
                }
            }
        } else {
            holder.binding.apply {
                tvComment.visibility = VISIBLE
                tilComment.visibility = GONE
                tiComment.visibility = GONE
                tvComment.text = session.comment
            }
        }
        val image = if (percent >= 80) R.drawable.ic_success else R.drawable.ic_repeat
        holder.binding.apply {
            tvSessionConstraint.text = session.criterion
            tvPercentage.text = evaluationText
            resultImageview.setImageResource(image)
        }
    }
}