package com.gf.tareas.utils;

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gf.tareas.data.Task

class DatabaseManager(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Task.SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(Task.SQL_DELETE_TABLE)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun onDestroy(db: SQLiteDatabase) {
        db.execSQL(Task.SQL_DELETE_TABLE)
    }

    companion object {
        const val DATABASE_NAME = "tasks.db"
        const val DATABASE_VERSION = 1
    }
}