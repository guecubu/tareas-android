package com.gf.tareas.data

data class Task(
        val id: Int, var title: String, var description: String, var date: String,
        var time: String, var done: Boolean = false, var categoryId: Int = 1
               ) {
    companion object {
        const val TABLE_NAME = "tasks"

        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_DATE = "date"
        const val COLUMN_TIME = "time"
        const val COLUMN_DONE = "done"
        const val COLUMN_CATEGORY_ID = "category_id"

        const val SQL_CREATE_TABLE =
                "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,$COLUMN_TITLE TEXT,$COLUMN_DESCRIPTION TEXT,$COLUMN_DATE TEXT,$COLUMN_TIME TEXT,$COLUMN_DONE INTEGER NOT NULL DEFAULT 0,$COLUMN_CATEGORY_ID INTEGER NOT NULL, FOREIGN KEY($COLUMN_CATEGORY_ID) REFERENCES ${Category.TABLE_NAME}(${Category.COLUMN_ID}) ON DELETE CASCADE)"
        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}

