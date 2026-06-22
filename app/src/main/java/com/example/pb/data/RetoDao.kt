package com.example.pb.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow

/**
 * RetoDao — Interfaz de acceso a datos para la tabla "retos" en SQLite.
 *
 * Room genera automáticamente el código de implementación de esta interfaz
 * en tiempo de compilación. No es necesario escribir el SQL completo para
 * INSERT, UPDATE y DELETE porque Room lo genera solo con las anotaciones.
 *
 * Todas las funciones son `suspend` (excepto getAll) para ejecutarse
 * dentro de Corrutinas y no bloquear el hilo principal de Android.
 */
@Dao
interface RetoDao {

    /**
     * Retorna TODOS los retos de la tabla ordenados del más nuevo al más antiguo.
     *
     * Retorna un [Flow]: esto significa que Room "escucha" la tabla continuamente.
     * Cada vez que se inserta, actualiza o elimina un reto, este Flow emite
     * automáticamente la lista actualizada al ViewModel sin necesidad de consultar de nuevo.
     *
     * Usado en: [RetosViewModel.cargarRetos]
     */
    @Query("SELECT * FROM retos ORDER BY id DESC")
    fun getAll(): Flow<List<Reto>>

    /**
     * Retorna UN reto escogido completamente al azar de la tabla.
     *
     * SQL: ORDER BY RANDOM() mezcla todos los registros aleatoriamente,
     * y LIMIT 1 toma solo el primero de esa mezcla. El resultado puede ser
     * null si la tabla está vacía (por eso el tipo de retorno es Reto?).
     *
     * Implementado para: HU 12 — Mostrar Reto Aleatorio.
     * Usado en: [HomeFragment.mostrarRetoAleatorio]
     */
    @Query("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomReto(): Reto?

    /**
     * Inserta un nuevo reto en la tabla.
     * Room genera automáticamente el SQL: INSERT INTO retos VALUES (...)
     */
    @Insert
    suspend fun insert(reto: Reto)

    /**
     * Actualiza un reto existente en la tabla usando su 'id' como referencia.
     * Room genera automáticamente el SQL: UPDATE retos SET ... WHERE id = ...
     */
    @Update
    suspend fun update(reto: Reto)

    /**
     * Elimina un reto específico de la tabla. (HU 9.0)
     * Room genera automáticamente el SQL: DELETE FROM retos WHERE id = ...
     * Es llamado desde [RetosViewModel.eliminarReto] dentro de una Corrutina.
     */
    @Delete
    suspend fun delete(reto: Reto)
}
