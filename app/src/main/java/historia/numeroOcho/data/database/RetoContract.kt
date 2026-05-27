package historia.numeroOcho.data.database


import android.provider.BaseColumns

object RetoContract {
    object RetoEntry : BaseColumns {
        const val TABLE_NAME = "retos"
        const val COLUMN_DESCRIPCION = "descripcion"

        const val SQL_CREATE_TABLE =
            "CREATE TABLE $TABLE_NAME (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_DESCRIPCION TEXT NOT NULL)"

        const val SQL_DELETE_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}