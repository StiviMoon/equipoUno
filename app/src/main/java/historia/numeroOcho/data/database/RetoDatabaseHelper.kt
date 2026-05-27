package historia.numeroOcho.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class RetoDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "retos.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(RetoContract.RetoEntry.SQL_CREATE_TABLE)
        // Datos de ejemplo para pruebas
        insertarDatosPrueba(db)
    }

    private fun insertarDatosPrueba(db: SQLiteDatabase) {
        val retosEjemplo = listOf(
            "Completar 10 pasos diarios",
            "Beber 2 litros de agua",
            "Meditar 5 minutos",
            "Leer 15 páginas"
        )

        retosEjemplo.forEach { descripcion ->
            val values = android.content.ContentValues().apply {
                put(RetoContract.RetoEntry.COLUMN_DESCRIPCION, descripcion)
            }
            db.insert(RetoContract.RetoEntry.TABLE_NAME, null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(RetoContract.RetoEntry.SQL_DELETE_TABLE)
        onCreate(db)
    }
}