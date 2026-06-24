package com.example.pb.repository

import com.example.pb.data.FakeRetoDao
import com.example.pb.model.Reto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RetoRepositoryTest {

    private lateinit var fakeDao: FakeRetoDao
    private lateinit var repository: RetoRepository

    @Before
    fun setUp() {
        fakeDao = FakeRetoDao()
        repository = RetoRepository(fakeDao)
    }

    @Test
    fun testInsertAndGetAllRetos() = runBlocking {
        // Given
        val reto1 = Reto(descripcion = "Hacer 10 flexiones de pecho")
        val reto2 = Reto(descripcion = "Cantar una canción graciosa")

        // When
        repository.insertReto(reto1)
        repository.insertReto(reto2)

        // Then
        val retosList = repository.getAllRetos().first()
        assertEquals("Should contain 2 retos", 2, retosList.size)
        // Check order DESC by id
        assertEquals("Second inserted reto (id=2) should be first", "Cantar una canción graciosa", retosList[0].descripcion)
        assertEquals("First inserted reto (id=1) should be second", "Hacer 10 flexiones de pecho", retosList[1].descripcion)
    }

    @Test
    fun testUpdateReto() = runBlocking {
        // Given
        val reto = Reto(descripcion = "Hacer 10 flexiones de pecho")
        repository.insertReto(reto)
        val initialList = repository.getAllRetos().first()
        val insertedReto = initialList[0]

        // When
        val updatedReto = insertedReto.copy(descripcion = "Hacer 20 flexiones de pecho")
        repository.updateReto(updatedReto)

        // Then
        val currentList = repository.getAllRetos().first()
        assertEquals("Should still have 1 reto", 1, currentList.size)
        assertEquals("Description should be updated", "Hacer 20 flexiones de pecho", currentList[0].descripcion)
    }

    @Test
    fun testDeleteReto() = runBlocking {
        // Given
        val reto = Reto(descripcion = "Hacer 10 flexiones de pecho")
        repository.insertReto(reto)
        val initialList = repository.getAllRetos().first()
        assertEquals("Should have 1 reto", 1, initialList.size)
        val insertedReto = initialList[0]

        // When
        repository.deleteReto(insertedReto)

        // Then
        val currentList = repository.getAllRetos().first()
        assertTrue("Should be empty after deletion", currentList.isEmpty())
    }
}
