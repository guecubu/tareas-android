package com.gf.tareas.data

data class Category(
        val id: Int,
        var name: String,
        var color: Int
) {
    companion object {
        const val TABLE_NAME = "categories"
        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_COLOR = "color"

        const val SQL_CREATE_TABLE =
                "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_NAME TEXT NOT NULL UNIQUE,$COLUMN_COLOR INTEGER NOT NULL)"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}