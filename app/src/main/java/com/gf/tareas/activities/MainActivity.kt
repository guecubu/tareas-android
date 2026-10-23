package com.gf.tareas.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.gf.tareas.R
import com.gf.tareas.adapters.TaskAdapter
import com.gf.tareas.data.Task
import com.gf.tareas.data.TaskDAO
import com.gf.tareas.databinding.ActivityMainBinding
import com.gf.tareas.databinding.DialogTaskBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var taskDAO: TaskDAO
    lateinit var adapter: TaskAdapter
    var tasks = listOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskDAO = TaskDAO(this)

        adapter = TaskAdapter(::onItemClick, ::onItemCheck, ::onItemEdit, ::onItemDelete)
        binding.recycledView.layoutManager = LinearLayoutManager(this)
        binding.recycledView.adapter = adapter

        binding.addFloatingActionButton.setOnClickListener {
            showAddTaskDialog(task = Task(-1, "", "", "", ""))
        }

        reloadData()
    }

    fun onItemClick(task: Task) {
        onItemCheck(task)
    }

    fun onItemCheck(task: Task) {
        //        task.done = !task.done
        taskDAO.update(task.copy(done = !task.done))

        reloadData()
    }

    fun onItemEdit(task: Task) {
        showAddTaskDialog(task.copy())
    }

    fun onItemDelete(task: Task) {

        val builder = MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar tarea")
                .setMessage("¿Estás seguro de que quieres eliminar esta tarea?")
                .setPositiveButton("Borrar") { dialog, which ->
                    taskDAO.delete(task)
                    reloadData()
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(false)
        val dialog = builder.create()
        dialog.show()

    }

    fun addNewTask(task: Task, bindingDialog: DialogTaskBinding) {

        task.title = bindingDialog.titleEditText.text?.toString() ?: ""
        task.description = bindingDialog.descriptionEditText.text?.toString() ?: ""
        task.date = bindingDialog.dateEditText.text?.toString() ?: ""
        task.time = bindingDialog.timeEditText.text?.toString() ?: ""

        taskDAO.save(task)

        // 2. Actualizar la lista local y notificar al adaptador
        reloadData()

    }

    fun showAddTaskDialog(task: Task) {

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

        val builder = MaterialAlertDialogBuilder(this)
                .setView(bindingDialog.root)
                .setTitle(title)
                .setPositiveButton(positiveButton) { dialog, which ->
                    addNewTask(task, bindingDialog)
                }
                .setNegativeButton("Cancelar") { dialog, which ->
                    dialog.dismiss()
                }
                .setCancelable(false)
        val dialog = builder.create()
        dialog.show()
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

    private fun reloadData() {
        tasks = taskDAO.findAll()
        adapter.submitList(tasks)
    }

}
