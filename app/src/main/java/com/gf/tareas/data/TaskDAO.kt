package com.gf.tareas.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
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

    fun getContentValues(task: Task): ContentValues {
        return ContentValues().apply {
            put(Task.COLUMN_TITLE, task.title)
            put(Task.COLUMN_DESCRIPTION, task.description)
            put(Task.COLUMN_DATE, task.date)
            put(Task.COLUMN_TIME, task.time)
            put(Task.COLUMN_DONE, task.done)
        }
    }

    fun cursorToEntity(cursor: Cursor): Task {
        with(cursor) {
            val id = getInt(getColumnIndexOrThrow(Task.COLUMN_ID))
            val title = getString(getColumnIndexOrThrow(Task.COLUMN_TITLE))
            val description = getString(getColumnIndexOrThrow(Task.COLUMN_DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(Task.COLUMN_DATE))
            val time = getString(getColumnIndexOrThrow(Task.COLUMN_TIME))
            val done = getInt(getColumnIndexOrThrow(Task.COLUMN_DONE)) != 0

            return Task(id, title, description, date, time, done)
        }
    }

    fun insert(task: Task) {
        open()
        val values = getContentValues(task)

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

        try {
            val id = db.update(Task.TABLE_NAME, values, "${Task.COLUMN_ID} = ${task.id}", null)
            Log.i(LOG_TAG, "Task updated with id $id")
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
            Log.i(LOG_TAG, "Task deleted with id $id")
        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }

    }

    fun findAll(): List<Task> {
        open()
        val tasks = mutableListOf<Task>()

        try {
            val cursor = db.query(Task.TABLE_NAME, null, null, null, null, null, null)
            Log.i(LOG_TAG, "Tasks found ${cursor.count}")
            with(cursor) {
                while (moveToNext()) {
                    tasks.add(cursorToEntity(cursor))
                }
            }

        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }
        return tasks

    }

    fun findById(id: Int): Task? {
        open()
        var task: Task? = null

        try {
            val cursor =
                    db.query(
                            Task.TABLE_NAME, null, "${Task.COLUMN_ID} = $id", null, null, null, null
                            )
            with(cursor) {
                if (moveToNext()) {
                    task = cursorToEntity(cursor)
                }
            }

        }
        catch (e: Exception) {
            Log.e(LOG_TAG, e.localizedMessage, e)
        }
        finally {
            close()
        }
        return task

    }
}