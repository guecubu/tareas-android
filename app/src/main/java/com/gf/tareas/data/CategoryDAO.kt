package com.gf.tareas.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.gf.tareas.utils.DatabaseManager

class CategoryDAO(context: Context) {
    private val manager = DatabaseManager(context)
    private lateinit var db: SQLiteDatabase

    fun open() {
        db = manager.writableDatabase
    }

    fun close() {
        db.close()
    }

    private fun getContentValues(category: Category) = ContentValues().apply {
        put(Category.COLUMN_NAME, category.name)
        put(Category.COLUMN_COLOR, category.color)
    }

    private fun cursorToEntity(cursor: Cursor) = with(cursor) {
        Category(
                getInt(getColumnIndexOrThrow(Category.COLUMN_ID)),
                getString(getColumnIndexOrThrow(Category.COLUMN_NAME)),
                getInt(getColumnIndexOrThrow(Category.COLUMN_COLOR))
        )
    }

    fun insert(category: Category): Long {
        open()
        return try {
            db.insertOrThrow(Category.TABLE_NAME, null, getContentValues(category))
        }
        finally {
            close()
        }
    }

    fun update(category: Category): Int {
        open()
        return try {
            db.update(Category.TABLE_NAME, getContentValues(category), "${Category.COLUMN_ID} = ?", arrayOf(category.id.toString()))
        }
        finally {
            close()
        }
    }

    fun save(category: Category): Long {
        return if (category.id == -1) insert(category) else update(category).toLong()
    }

    fun delete(category: Category): Int = delete(category.id)

    fun delete(id: Int): Int {
        open()
        return try {
            db.delete(Category.TABLE_NAME, "${Category.COLUMN_ID} = ?", arrayOf(id.toString()))
        }
        finally {
            close()
        }
    }

    fun findAll(): List<Category> {
        open()
        return try {
            db.query(Category.TABLE_NAME, null, null, null, null, null, Category.COLUMN_NAME).use { cursor ->
                buildList {
                    while (cursor.moveToNext()) add(cursorToEntity(cursor))
                }
            }
        }
        finally {
            close()
        }
    }

    fun findById(id: Int): Category? {
        open()
        return try {
            db.query(Category.TABLE_NAME, null, "${Category.COLUMN_ID} = ?", arrayOf(id.toString()), null, null, null).use { cursor ->
                if (cursor.moveToFirst()) cursorToEntity(cursor) else null
            }
        }
        finally {
            close()
        }
    }
}