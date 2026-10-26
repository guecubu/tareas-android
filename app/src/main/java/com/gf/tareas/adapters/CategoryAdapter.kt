package com.gf.tareas.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gf.tareas.R
import com.gf.tareas.data.Category

class CategoryAdapter(
        private val onEdit: (Category) -> Unit,
        private val onDelete: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.name.text = category.name
        holder.color.background = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(category.color)
        }
        holder.edit.setOnClickListener { onEdit(category) }
        holder.delete.setOnClickListener { onDelete(category) }
    }

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.categoryNameTextView)
        val color: View = view.findViewById(R.id.colorView)
        val edit: View = view.findViewById(R.id.editCategoryButton)
        val delete: View = view.findViewById(R.id.deleteCategoryButton)
    }

    private object CategoryDiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem
    }
}