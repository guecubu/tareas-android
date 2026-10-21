package com.gf.tareas.data

data class Task(val id: Int, val title: String, val description: String, val date: String,
                val time: String, val done: Boolean) {
    companion object {
        const val TABLE_NAME = "tasks"

        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DONE = "done"

        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," + "$COLUMN_TITLE TEXT," + "$COLUMN_DESCRIPTION TEXT," + "$COLUMN_DATE TEXT," + "$COLUMN_TIME TEXT," + "$COLUMN_DONE INTEGER)"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}

