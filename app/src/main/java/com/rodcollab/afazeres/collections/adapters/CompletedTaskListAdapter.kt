package com.rodcollab.afazeres.collections.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodcollab.afazeres.collections.TaskListViewModel
import com.rodcollab.afazeres.collections.model.TaskItem
import com.rodcollab.afazeres.databinding.TaskItemBinding

class CompletedTaskListAdapter(
    private val viewModel: TaskListViewModel
) : RecyclerView.Adapter<CompletedTaskListAdapter.ViewHolder>() {

    private val asyncListDiffer: AsyncListDiffer<TaskItem> = AsyncListDiffer(this, DiffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(asyncListDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncListDiffer.currentList.size

    fun updateTasks(habits: List<TaskItem>) {
        asyncListDiffer.submitList(habits)
    }

    class ViewHolder(
        private val binding: TaskItemBinding,
        private val viewModel: TaskListViewModel,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(habit: TaskItem) {
            binding.title.text = habit.title
            binding.completeCheckBox.isChecked = habit.isCompleted
            binding.completeCheckBox.setOnClickListener {
                viewModel.toggleTaskCompleted(habit.id, habit.isCompleted)
            }
        }

    }

    object DiffCallback : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem == newItem
        }
    }
}