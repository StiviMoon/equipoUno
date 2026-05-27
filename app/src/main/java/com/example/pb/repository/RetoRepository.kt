package com.example.pb.repository

import com.example.pb.data.RetoDao
import com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow

class RetoRepository(private val dao: RetoDao) {

    fun getAllRetos(): Flow<List<Reto>> = dao.getAll()

    suspend fun insertReto(reto: Reto) = dao.insert(reto)

    suspend fun updateReto(reto: Reto) = dao.update(reto)

    suspend fun deleteReto(reto: Reto) = dao.delete(reto)
}
