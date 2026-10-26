package com.gf.tareas.utils

import com.gf.tareas.data.Task
import com.gf.tareas.data.TaskDAO
import com.gf.tareas.data.Category
import com.gf.tareas.data.CategoryDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SampleDataSeeder {

    fun seedIfEmpty(taskDAO: TaskDAO) {
        val categoryDAO = CategoryDAO(taskDAO.context)
        val categoriesByName = categoryDAO.findAll().associateBy { it.name }.toMutableMap()
        listOf(
                Category(-1, "Personal", 0xFF4CAF50.toInt()),
                Category(-1, "Trabajo", 0xFF2196F3.toInt()),
                Category(-1, "Salud", 0xFFFF9800.toInt())
              ).forEach { category ->
            if (category.name !in categoriesByName) {
                val id = categoryDAO.insert(category).toInt()
                categoriesByName[category.name] = category.copy(id = id)
            }
        }
        if (taskDAO.findAll().isNotEmpty()) return

        val personalCategoryId = categoriesByName.getValue("Personal").id
        val workCategoryId = categoriesByName.getValue("Trabajo").id
        val healthCategoryId = categoriesByName.getValue("Salud").id

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun getDateOffset(daysOffset: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
            return sdf.format(calendar.time)
        }

        val sampleTasks = listOf(
                Task(
                        -1, "Pagar factura de luz", "Revisar cuenta bancaria y abonar recibo",
                        getDateOffset(-2), "09:00", false, personalCategoryId
                    ),
                Task(
                        -1, "Estudiar tema 5 de oposiciones",
                        "Repasar tema y hacer test de evaluación", getDateOffset(-1), "20:00", false, workCategoryId
                    ),
                Task(
                        -1, "Llamar al médico", "Pedir cita para la revisión anual",
                        getDateOffset(0), "11:30", false, healthCategoryId
                    ),
                Task(
                        -1, "Llevar coche al taller", "Cambio de aceite y filtros de revisión",
                        getDateOffset(0), "17:00", true, personalCategoryId
                    ),
                Task(
                        -1, "Comprar fruta y verdura", "Manzanas, plátanos, lechuga y tomates",
                        getDateOffset(1), "10:00", false, personalCategoryId
                    ),
                Task(
                        -1, "Hacer rutina de ejercicio", "30 minutos de cardio y estiramientos",
                        getDateOffset(1), "18:00", false, healthCategoryId
                    ),
                Task(
                        -1, "Reunión de equipo semanal", "Planificación del proyecto y tareas",
                        getDateOffset(2), "16:30", false, workCategoryId
                    ),
                Task(
                        -1, "Revisar correos pendientes", "Responder correos urgentes de trabajo",
                        getDateOffset(0), "14:00", true, workCategoryId
                    ),
                Task(
                        -1, "Renovar el DNI en comisaría", "Traer fotos y cita previa confirmada",
                        getDateOffset(5), "12:00", false, personalCategoryId
                    ),
                Task(
                        -1, "Preparar cena familiar", "Comprar ingredientes para pizza casera",
                        getDateOffset(0), "21:00", false, personalCategoryId
                    )
                                )

        sampleTasks.forEach { taskDAO.save(it) }
    }
}
