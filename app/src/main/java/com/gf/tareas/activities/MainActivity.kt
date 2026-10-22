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
        tasks = taskDAO.findAll()

        adapter = TaskAdapter(tasks, ::onItemClick, ::onItemCheck, ::onItemEdit, ::onItemDelete)
        binding.recycledView.layoutManager = LinearLayoutManager(this)
        binding.recycledView.adapter = adapter
        binding.addFloatingActionButton.setOnClickListener {
            addNewTaskManual("Nueva tarea manual ${tasks.size + 1}")
        }
    }

    fun onItemClick(position: Int) {
        onItemCheck(position)
    }

    fun onItemCheck(position: Int) {
        val task = tasks[position]
        task.done = !task.done
        taskDAO.update(task)

        // TODO: Se puede optimizar
        tasks = taskDAO.findAll()
        adapter.updateData(tasks) //        adapter.notifyItemChanged(position)
    }

    fun onItemEdit(position: Int) {
        val task = tasks[position]
        taskDAO.update(task)
        tasks = taskDAO.findAll()
        adapter.updateData(tasks) //        adapter.notifyItemChanged(position)
    }

    fun onItemDelete(position: Int) {
        val task = tasks[position]
        taskDAO.delete(task)
        tasks = taskDAO.findAll()
        adapter.updateData(tasks) //        adapter.notifyItemChanged(position)
    }

    fun addNewTaskManual(title: String) {
        // 1. Crear nueva tarea o mostrar un Dialog / AlertDialog para pedir el título
        val newTask = Task(-1, title, "", "", "")
        taskDAO.insert(newTask)

        // 2. Actualizar la lista local y notificar al adaptador
        tasks = taskDAO.findAll()
        adapter.updateData(tasks)

    }

}