package com.gf.tareas.utils

import com.gf.tareas.data.Task
import com.gf.tareas.data.TaskDAO
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SampleDataSeeder {

    fun seedIfEmpty(taskDAO: TaskDAO) {
        if (taskDAO.findAll().isNotEmpty()) return

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        fun getDateOffset(daysOffset: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, daysOffset)
            return sdf.format(calendar.time)
        }

        val sampleTasks = listOf(
                Task(
                        -1, "Pagar factura de luz", "Revisar cuenta bancaria y abonar recibo",
                        getDateOffset(-2), "09:00", false
                    ),
                Task(
                        -1, "Estudiar tema 5 de oposiciones",
                        "Repasar tema y hacer test de evaluación", getDateOffset(-1), "20:00", false
                    ),
                Task(
                        -1, "Llamar al médico", "Pedir cita para la revisión anual",
                        getDateOffset(0), "11:30", false
                    ),
                Task(
                        -1, "Llevar coche al taller", "Cambio de aceite y filtros de revisión",
                        getDateOffset(0), "17:00", true
                    ),
                Task(
                        -1, "Comprar fruta y verdura", "Manzanas, plátanos, lechuga y tomates",
                        getDateOffset(1), "10:00", false
                    ),
                Task(
                        -1, "Hacer rutina de ejercicio", "30 minutos de cardio y estiramientos",
                        getDateOffset(1), "18:00", false
                    ),
                Task(
                        -1, "Reunión de equipo semanal", "Planificación del proyecto y tareas",
                        getDateOffset(2), "16:30", false
                    ),
                Task(
                        -1, "Revisar correos pendientes", "Responder correos urgentes de trabajo",
                        getDateOffset(0), "14:00", true
                    ),
                Task(
                        -1, "Renovar el DNI en comisaría", "Traer fotos y cita previa confirmada",
                        getDateOffset(5), "12:00", false
                    ),
                Task(
                        -1, "Preparar cena familiar", "Comprar ingredientes para pizza casera",
                        getDateOffset(0), "21:00", false
                    )
                                )

        sampleTasks.forEach { taskDAO.save(it) }
    }
}
