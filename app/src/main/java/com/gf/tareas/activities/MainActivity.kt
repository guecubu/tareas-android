package com.gf.tareas.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gf.tareas.R
import com.gf.tareas.adapters.TaskAdapter
import com.gf.tareas.data.Task
import com.gf.tareas.data.TaskDAO
import com.gf.tareas.databinding.ActivityMainBinding
import com.gf.tareas.databinding.DialogTaskBinding
import com.gf.tareas.utils.SampleDataSeeder
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    //region Properties
    private lateinit var binding: ActivityMainBinding
    private lateinit var taskDAO: TaskDAO
    private lateinit var adapter: TaskAdapter
    private var tasks = listOf<Task>()
    private var sortCriteria = SortCriteria.DATE

    private enum class SortCriteria {
        DATE, CREATION, TITLE, STATUS
    }
    //endregion

    //region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupActionBar()
        setupRecyclerView()
        setupListeners()

        taskDAO = TaskDAO(this)
        reloadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_order -> {
                showSortDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    //endregion

    //region Setup UI
    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(::onItemClick, ::onItemCheck, ::onItemEdit, ::onItemDelete)
        binding.recycledView.layoutManager = LinearLayoutManager(this)
        binding.recycledView.adapter = adapter

        // En setupRecyclerView() de MainActivity.kt
        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL).apply {
            dividerColor =
                    getColor(
                            this@MainActivity,
                            com.google.android.material.R.color.design_default_color_on_secondary
                            )
            dividerThickness = 2
        }
        binding.recycledView.addItemDecoration(divider)
    }

    private fun setupListeners() {
        binding.addFloatingActionButton.setOnClickListener {
            showAddTaskDialog(task = Task(-1, "", "", "", ""))
        }
        com.gf.tareas.utils.BottomNavigationHelper.setupBottomNavigation(
                this,
                binding.bottomNavigationView,
                R.id.navigation_tasks
                                                                        )
    }
    //endregion

    //region Data & Sorting
    private fun reloadData() {
        SampleDataSeeder.seedIfEmpty(taskDAO)
        tasks = taskDAO.findAll()
        applySort()
    }

    private fun applySort() {
        val sortedTasks = when (sortCriteria) {
            SortCriteria.DATE -> tasks.sortedBy { parseDate(it.date) }
            SortCriteria.CREATION -> tasks.sortedBy { it.id }
            SortCriteria.TITLE -> tasks.sortedBy { it.title.lowercase() }
            SortCriteria.STATUS -> tasks.sortedWith(
                    compareBy<Task> { if (it.done) 1 else 0 }
                            .thenBy { parseDate(it.date) }
                                                   )
        }
        adapter.submitList(sortedTasks)
    }

    private fun parseDate(dateStr: String): Long {
        if (dateStr.isBlank()) return Long.MAX_VALUE
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: Long.MAX_VALUE
        }
        catch (_: Exception) {
            Long.MAX_VALUE
        }
    }

    private fun showSortDialog() {
        val options = arrayOf(
                getString(R.string.sort_date),
                getString(R.string.sort_creation),
                getString(R.string.sort_title),
                getString(R.string.sort_status)
                             )
        var checkedItem = when (sortCriteria) {
            SortCriteria.DATE -> 0
            SortCriteria.CREATION -> 1
            SortCriteria.TITLE -> 2
            SortCriteria.STATUS -> 3
        }

        MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sort_dialog_title)
                .setSingleChoiceItems(options, checkedItem) { _, which ->
                    checkedItem = which
                }
                .setPositiveButton(R.string.order_button) { _, _ ->
                    sortCriteria = when (checkedItem) {
                        0 -> SortCriteria.DATE
                        1 -> SortCriteria.CREATION
                        2 -> SortCriteria.TITLE
                        3 -> SortCriteria.STATUS
                        else -> SortCriteria.DATE
                    }
                    applySort()
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }
    //endregion

    //region Task Item Actions (Adapter Callbacks)
    private fun onItemClick(task: Task) {
        onItemCheck(task)
    }

    private fun onItemCheck(task: Task) {
        taskDAO.update(task.copy(done = !task.done))
        reloadData()
    }

    private fun onItemEdit(task: Task) {
        showAddTaskDialog(task.copy())
    }

    private fun onItemDelete(task: Task) {
        MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                .setPositiveButton("Borrar") { _, _ ->
                    taskDAO.delete(task)
                    reloadData()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
    }
    //endregion

    //region Dialogs & Pickers
    private fun showAddTaskDialog(task: Task) {
        val isCreate = task.id == -1
        val title = if (isCreate) "Nueva tarea" else "Editar tarea"
        val positiveButton = if (isCreate) "Crear" else "Guardar"

        val bindingDialog = DialogTaskBinding.inflate(layoutInflater)

        if (!isCreate) {
            bindingDialog.titleEditText.setText(task.title)
            bindingDialog.descriptionEditText.setText(task.description)
            bindingDialog.dateEditText.setText(task.date)
            bindingDialog.timeEditText.setText(task.time)
        }

        bindingDialog.dateEditText.setOnClickListener {
            showDatePicker(bindingDialog)
        }

        bindingDialog.timeEditText.setOnClickListener {
            showTimePicker(bindingDialog)
        }

        MaterialAlertDialogBuilder(this)
                .setView(bindingDialog.root)
                .setTitle(title)
                .setPositiveButton(positiveButton) { _, _ ->
                    addNewTask(task, bindingDialog)
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .show()
    }

    private fun addNewTask(task: Task, bindingDialog: DialogTaskBinding) {
        task.title = bindingDialog.titleEditText.text?.toString() ?: ""
        task.description = bindingDialog.descriptionEditText.text?.toString() ?: ""
        task.date = bindingDialog.dateEditText.text?.toString() ?: ""
        task.time = bindingDialog.timeEditText.text?.toString() ?: ""

        taskDAO.save(task)
        reloadData()
    }

    private fun showDatePicker(bindingDialog: DialogTaskBinding) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona la fecha")
                .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("UTC")
            bindingDialog.dateEditText.setText(format.format(calendar.time))
        }

        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker(bindingDialog: DialogTaskBinding) {
        val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Selecciona la hora")
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val formattedTime = String.format(
                    Locale.getDefault(), "%02d:%02d", timePicker.hour, timePicker.minute
                                             )
            bindingDialog.timeEditText.setText(formattedTime)
        }

        timePicker.show(supportFragmentManager, "TIME_PICKER")
    }
    //endregion

}
