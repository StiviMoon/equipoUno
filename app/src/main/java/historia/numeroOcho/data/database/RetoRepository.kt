package historia.numeroOcho.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import historia.numeroOcho.data.model.Reto
import android.provider.BaseColumns

class RetoRepository(private val context: Context) {

    private val dbHelper = RetoDatabaseHelper(context)

    fun obtenerRetoPorId(id: Long): Reto? {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            RetoContract.RetoEntry.COLUMN_DESCRIPCION
        )

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(id.toString())

        val cursor = db.query(
            RetoContract.RetoEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var reto: Reto? = null
        if (cursor.moveToFirst()) {
            val idReto = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(RetoContract.RetoEntry.COLUMN_DESCRIPCION))
            reto = Reto(idReto, descripcion)
        }
        cursor.close()
        db.close()
        return reto
    }

    fun actualizarReto(reto: Reto): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(RetoContract.RetoEntry.COLUMN_DESCRIPCION, reto.descripcion)
        }

        val selection = "${BaseColumns._ID} = ?"
        val selectionArgs = arrayOf(reto.id.toString())

        val rowsUpdated = db.update(
            RetoContract.RetoEntry.TABLE_NAME,
            values,
            selection,
            selectionArgs
        )

        db.close()
        return rowsUpdated > 0
    }

    fun obtenerTodosLosRetos(): List<Reto> {
        val retos = mutableListOf<Reto>()
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            RetoContract.RetoEntry.COLUMN_DESCRIPCION
        )

        val cursor = db.query(
            RetoContract.RetoEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(RetoContract.RetoEntry.COLUMN_DESCRIPCION))
            retos.add(Reto(id, descripcion))
        }
        cursor.close()
        db.close()
        return retos
    }
}