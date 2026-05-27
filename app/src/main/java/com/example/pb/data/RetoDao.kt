package com.example.pb.data

import androidx.room.*
import com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow


@Dao
interface RetoDao {

    @Query("SELECT * FROM retos ORDER BY timestamp DESC")
    fun getAllRetos() : Flow<List<Reto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReto (reto : Reto)


    @Update
    suspend fun  updateReto(reto:Reto)


    @Delete
    suspend fun deleteReto(reto:Reto)

}