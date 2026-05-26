package com.example.pb.repository

import com.example.pb.data.RetoDao
import  com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow

class RetoRepository(private val retoDao: RetoDao) {
    fun getAllRetos(): Flow<List<Reto>> = retoDao.getAllRetos()

    suspend fun insertReto(reto: Reto) = retoDao.insertReto(reto)

    suspend fun  updateReto (reto: Reto) = retoDao.updateReto(reto)

    suspend fun  deleteReto(reto: Reto) = retoDao.deleteReto(reto)
}