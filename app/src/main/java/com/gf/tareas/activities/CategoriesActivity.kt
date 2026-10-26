package com.gf.tareas.activities

import android.os.Bundle
import android.database.sqlite.SQLiteConstraintException
import android.graphics.Color
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gf.tareas.R
import com.gf.tareas.adapters.CategoryAdapter
import com.gf.tareas.data.Category
import com.gf.tareas.data.CategoryDAO
import com.gf.tareas.databinding.ActivityCategoriesBinding
import com.gf.tareas.utils.BottomNavigationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class CategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private lateinit var categoryDAO: CategoryDAO
    private lateinit var adapter: CategoryAdapter
    private var categories = emptyList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()
        setupBottomNavigation()
        categoryDAO = CategoryDAO(this)
        adapter = CategoryAdapter(::showCategoryDialog, ::confirmDelete)
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.categoriesRecyclerView.adapter = adapter
        binding.addCategoryButton.setOnClickListener { showCategoryDialog(null) }
        refreshCategories()
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(
                this,
                binding.bottomNavigationView,
                R.id.navigation_categories
                                                    )
    }

    private fun refreshCategories() {
        categories = categoryDAO.findAll()
        adapter.submitList(categories)
    }

    private fun showCategoryDialog(category: Category?) {
        val nameInput = EditText(this).apply {
            hint = getString(R.string.category_name)
            setText(category?.name.orEmpty())
            setSingleLine(true)
        }
        var selectedColor = category?.color ?: Color.RED
        val colorButton = com.google.android.material.button.MaterialButton(this).apply {
            text = getString(R.string.choose_color)
            setBackgroundColor(selectedColor)
        }
        val content = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            val padding = (20 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding / 2, padding, 0)
            addView(nameInput)
            addView(colorButton)
        }
        colorButton.setOnClickListener {
            val colorPickerDialog = ColorPickerDialog.Builder(this)
                    .setTitle(R.string.choose_color)
                    .setPositiveButton(
                            getString(android.R.string.ok),
                            ColorEnvelopeListener { envelope, _ ->
                                selectedColor = envelope.color
                                colorButton.setBackgroundColor(selectedColor)
                            }
                                     )
                    .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            colorPickerDialog.colorPickerView.setInitialColor(selectedColor)
            colorPickerDialog.show()
        }
        val dialog = MaterialAlertDialogBuilder(this)
                .setTitle(if (category == null) R.string.add_category else R.string.edit_category)
                .setView(content)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, null)
                .create()
        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = nameInput.text.toString().trim()
                if (name.isEmpty()) {
                    nameInput.error = getString(R.string.category_name_required)
                    return@setOnClickListener
                }
                val updated = Category(category?.id ?: -1, name, selectedColor)
                try {
                    categoryDAO.save(updated)
                    refreshCategories()
                    dialog.dismiss()
                }
                catch (_: SQLiteConstraintException) {
                    nameInput.error = getString(R.string.category_name_exists)
                }
            }
        }
        dialog.show()
    }

    private fun confirmDelete(category: Category) {
        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_category_title)
                .setMessage(R.string.delete_category_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete) { _, _ ->
                    categoryDAO.delete(category)
                    refreshCategories()
                }
                .show()
    }

}
