package com.example.pb.data

import com.example.pb.model.Reto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeRetoDao : RetoDao {
    private val database = mutableListOf<Reto>()
    private val _retosFlow = MutableStateFlow<List<Reto>>(emptyList())

    override fun getAll(): Flow<List<Reto>> {
        return _retosFlow.asStateFlow()
    }

    override suspend fun getRandomReto(): Reto? {
        if (database.isEmpty()) return null
        return database.random()
    }

    override suspend fun insert(reto: Reto) {
        val newId = if (reto.id == 0) {
            (database.maxOfOrNull { it.id } ?: 0) + 1
        } else {
            reto.id
        }
        val item = reto.copy(id = newId)
        database.add(item)
        updateFlow()
    }

    override suspend fun update(reto: Reto) {
        val index = database.indexOfFirst { it.id == reto.id }
        if (index != -1) {
            database[index] = reto
            updateFlow()
        }
    }

    override suspend fun delete(reto: Reto) {
        database.removeAll { it.id == reto.id }
        updateFlow()
    }

    private fun updateFlow() {
        // Sort descending to match Room DAO SQL behavior: "ORDER BY id DESC"
        _retosFlow.value = database.sortedByDescending { it.id }
    }
}
