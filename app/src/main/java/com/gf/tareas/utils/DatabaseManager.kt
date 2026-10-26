package com.gf.tareas.utils;

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.gf.tareas.data.Category
import com.gf.tareas.data.Task

class DatabaseManager(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(Category.SQL_CREATE_TABLE)
        db.execSQL(Task.SQL_CREATE_TABLE)
        db.execSQL("INSERT INTO ${Category.TABLE_NAME} (${Category.COLUMN_ID}, ${Category.COLUMN_NAME}, ${Category.COLUMN_COLOR}) VALUES (1, 'General', -1)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.beginTransaction()
            try {
                db.execSQL(Category.SQL_CREATE_TABLE)
                db.execSQL("INSERT INTO ${Category.TABLE_NAME} (${Category.COLUMN_ID}, ${Category.COLUMN_NAME}, ${Category.COLUMN_COLOR}) VALUES (1, 'General', -1)")
                db.execSQL("ALTER TABLE ${Task.TABLE_NAME} RENAME TO ${Task.TABLE_NAME}_legacy")
                db.execSQL(Task.SQL_CREATE_TABLE)
                db.execSQL("INSERT INTO ${Task.TABLE_NAME} (${Task.COLUMN_ID}, ${Task.COLUMN_TITLE}, ${Task.COLUMN_DESCRIPTION}, ${Task.COLUMN_DATE}, ${Task.COLUMN_TIME}, ${Task.COLUMN_DONE}, ${Task.COLUMN_CATEGORY_ID}) SELECT ${Task.COLUMN_ID}, ${Task.COLUMN_TITLE}, ${Task.COLUMN_DESCRIPTION}, ${Task.COLUMN_DATE}, ${Task.COLUMN_TIME}, ${Task.COLUMN_DONE}, 1 FROM ${Task.TABLE_NAME}_legacy")
                db.execSQL("DROP TABLE ${Task.TABLE_NAME}_legacy")
                db.setTransactionSuccessful()
            }
            finally {
                db.endTransaction()
            }
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun onDestroy(db: SQLiteDatabase) {
        db.execSQL(Task.SQL_DELETE_TABLE)
    }

    companion object {
        const val DATABASE_NAME = "tasks.db"
        const val DATABASE_VERSION = 2
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
}