package com.gf.tareas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gf.tareas.data.Task
import com.gf.tareas.databinding.ItemTaskBinding

class TaskAdapter(
        private var items: List<Task>, private val onItemClick: (position: Int) -> Unit,
        private val onItemCheck: (position: Int) -> Unit,
        private val onItemEdit: (position: Int) -> Unit,
        private val onItemDelete: (position: Int) -> Unit
                 ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = items[position]
        holder.bind(task)

        holder.itemView.setOnClickListener {
            //            onItemClick(position)
        }

        holder.binding.doneCheckBox.setOnCheckedChangeListener { _, _ ->
            if (holder.binding.doneCheckBox.isPressed) {
                onItemCheck(position)
            }
        }

        holder.binding.editButton.setOnClickListener {
            onItemEdit(position)
        }

        holder.binding.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(dataList: List<Task>) {
        items = dataList
        notifyDataSetChanged()
    }

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.titleTextView.text = task.title
            binding.doneCheckBox.isChecked = task.done
        }
    }

}
