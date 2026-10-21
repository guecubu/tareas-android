package com.gf.tareas.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.gf.tareas.utils.DatabaseManager

private const val LOG_TAG = "TaskDAO"

class TaskDAO(val context: Context) {

    lateinit var db: SQLiteDatabase
    private val manager = DatabaseManager(context)

    fun open() {
        db = manager.writableDatabase
    }

    fun close() {
        db.close()
    }

    fun insert(task: Task) {
        open()
        val values = ContentValues().apply {
            put(Task.COLUMN_TITLE, task.title)
            put(Task.COLUMN_DESCRIPTION, task.description)
            put(Task.COLUMN_DATE, task.date)
            put(Task.COLUMN_TIME, task.time)
            put(Task.COLUMN_DONE, task.done)
        }

        try {
            val id = db.insertOrThrow(Task.TABLE_NAME, null, values)
            Log.i(LOG_TAG, "Task inserted with id $id")
        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }
    }

    fun update(task: Task) {
        open()
        val values = ContentValues().apply {
            put(Task.COLUMN_TITLE, task.title)
            put(Task.COLUMN_DESCRIPTION, task.description)
            put(Task.COLUMN_DATE, task.date)
            put(Task.COLUMN_TIME, task.time)
            put(Task.COLUMN_DONE, task.done)
        }
        val selection = "${Task.COLUMN_ID} = ?"
        val selectionArgs = arrayOf(task.id.toString())

        try {
            val id = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_ID} = ${task.id}", null)
            Log.i(LOG_TAG, "Task inserted with id $id")
        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }

    }

    fun delete(task: Task) {
        delete(task.id)
    }

    fun delete(id: Int) {
        open()

        try {
            val deletedRows = db.delete(Task.TABLE_NAME, "${Task.COLUMN_ID} = $id", null)
            Log.i(LOG_TAG, "Task inserted with id $id")
        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }

    }

    fun findAll(): List<Task> {
        val cursor = db.query(Task.TABLE_NAME, null, null, null, null, null, null)
        return TODO("Provide the return value")
    }

    fun findById(id: Int): Task? {
        val cursor =
            db.query(Task.TABLE_NAME, null, "${Task.COLUMN_ID} = ?", arrayOf(id.toString()), null,
                    null, null)
        return TODO("Provide the return value")
    }

}