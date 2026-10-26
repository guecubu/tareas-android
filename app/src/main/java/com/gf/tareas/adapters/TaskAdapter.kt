package com.gf.tareas.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gf.tareas.R
import com.gf.tareas.data.Task
import com.gf.tareas.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

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
        holder.bind(task.copy())

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

            val context = binding.root.context
            val urgency = if (task.done) Urgency.NORMAL else getTaskUrgency(task.date)

            val backgroundColor = when (urgency) {
                Urgency.OVERDUE -> ContextCompat.getColor(context, R.color.pastel_red)
                Urgency.DUE_TOMORROW -> ContextCompat.getColor(context, R.color.pastel_yellow)
                Urgency.NORMAL -> Color.TRANSPARENT
            }

            if (urgency == Urgency.NORMAL) {
                val typedValue = TypedValue()
                context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground, typedValue, true
                                              )
                binding.root.setBackgroundResource(typedValue.resourceId)
            }
            else {
                val colorDrawable = ColorDrawable(backgroundColor)
                val typedValue = TypedValue()
                context.theme.resolveAttribute(
                        android.R.attr.selectableItemBackground, typedValue, true
                                              )
                val rippleDrawable = ContextCompat.getDrawable(context, typedValue.resourceId)

                val layerDrawable = LayerDrawable(arrayOf(colorDrawable, rippleDrawable))
                binding.root.background = layerDrawable
            }
        }

        private fun getTaskUrgency(dateStr: String): Urgency {
            if (dateStr.isBlank()) return Urgency.NORMAL
            return try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.parse(dateStr) ?: return Urgency.NORMAL

                val taskCalendar = Calendar.getInstance().apply {
                    time = date
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val todayCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }

                val diffMillis = taskCalendar.timeInMillis - todayCalendar.timeInMillis
                val diffDays = diffMillis / (1000 * 60 * 60 * 24)

                when {
                    diffDays < 0 -> Urgency.OVERDUE
                    diffDays == 1L -> Urgency.DUE_TOMORROW
                    else -> Urgency.NORMAL
                }
            }
            catch (_: Exception) {
                Urgency.NORMAL
            }
        }

        enum class Urgency {
            OVERDUE, DUE_TOMORROW, NORMAL
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
                oldItem.date != newItem.date -> false
                else -> true
            }
        }
    }

}
