package com.gf.tareas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gf.tareas.data.Task
import com.gf.tareas.databinding.ItemTaskBinding

class TaskAdapter(
        private val onItemClick: (task: Task) -> Unit,
        private val onItemCheck: (task: Task) -> Unit,
        private val onItemEdit: (task: Task) -> Unit,
        private val onItemDelete: (task: Task) -> Unit
                 ) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)

        holder.itemView.setOnClickListener {
            onItemClick(task)
        }

        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, _ ->
            if (holder.binding.doneCheckBox.isPressed) {
                onItemCheck(task)
            }
        }

        holder.binding.editButton.setOnClickListener {
            onItemEdit(task)
        }

        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(task)
        }
    }

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.titleTextView.text = task.title
            binding.doneCheckBox.isChecked = task.done
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return when {
                oldItem.title != newItem.title -> false
                oldItem.done != newItem.done -> false
                else -> true
            }
        }
    }

}
