package com.example.pb.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow

@Dao
interface RetoDao {

    @Query("SELECT * FROM retos ORDER BY id DESC")
    fun getAll(): Flow<List<Reto>>

    @Query("SELECT * FROM retos ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomReto(): Reto?

    @Insert
    suspend fun insert(reto: Reto)

    @Update
    suspend fun update(reto: Reto)

    @Delete
    suspend fun delete(reto: Reto)
}
