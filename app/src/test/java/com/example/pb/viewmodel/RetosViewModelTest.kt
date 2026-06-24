package com.example.pb.viewmodel

import app.cash.turbine.test
import com.example.pb.data.model.Reto
import com.example.pb.data.repository.RetosRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class RetosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: RetosRepository

    @Mock
    private lateinit var firebaseAuth: FirebaseAuth

    @Mock
    private lateinit var firebaseUser: FirebaseUser

    private lateinit var viewModel: RetosViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        whenever(firebaseAuth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn("user123")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadingStateInitially() = runTest {
        // Given
        whenever(repository.getRetosFlow()).thenReturn(flow {
            // Emite despues de delay para capturar estado Loading
            kotlinx.coroutines.delay(100)
            emit(emptyList<Reto>())
        })

        // When
        viewModel = RetosViewModel(repository, firebaseAuth)

        // Then
        viewModel.uiState.test {
            assertEquals(RetosUiState.Loading, awaitItem())
            testScheduler.advanceTimeBy(100)
            assertEquals(RetosUiState.Empty, awaitItem())
        }
    }

    @Test
    fun testEmptyState() = runTest {
        // Given
        whenever(repository.getRetosFlow()).thenReturn(flowOf(emptyList()))

        // When
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            assertEquals(RetosUiState.Empty, awaitItem())
        }
    }

    @Test
    fun testSuccessState() = runTest {
        // Given
        val list = listOf(Reto(id = "1", descripcion = "Descr 1", uidUsuario = "user123"))
        whenever(repository.getRetosFlow()).thenReturn(flowOf(list))

        // When
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val item = awaitItem()
            assertTrue(item is RetosUiState.Success)
            assertEquals(list, (item as RetosUiState.Success).retos)
        }
    }

    @Test
    fun testErrorState() = runTest {
        // Given
        val exception = RuntimeException("Error de Firestore")
        whenever(repository.getRetosFlow()).thenReturn(flow {
            throw exception
        })

        // When
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val item = awaitItem()
            assertTrue(item is RetosUiState.Error)
            assertEquals(exception, (item as RetosUiState.Error).exception)
        }
    }

    @Test
    fun testInsertarReto() = runTest {
        // Given
        whenever(repository.getRetosFlow()).thenReturn(flowOf(emptyList()))
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        val desc = "Nuevo Reto Test"

        // When
        viewModel.insertarReto(desc)
        testScheduler.advanceUntilIdle()

        // Then
        verify(repository).insertReto(argThat {
            descripcion == desc && uidUsuario == "user123"
        })
    }

    @Test
    fun testEditarReto() = runTest {
        // Given
        whenever(repository.getRetosFlow()).thenReturn(flowOf(emptyList()))
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        val reto = Reto(id = "1", descripcion = "Editado", uidUsuario = "user123")

        // When
        viewModel.editarReto(reto)
        testScheduler.advanceUntilIdle()

        // Then
        verify(repository).updateReto(reto)
    }

    @Test
    fun testEliminarRetoSuccess() = runTest {
        // Given
        val list = listOf(Reto(id = "1", descripcion = "Reto a borrar", uidUsuario = "user123"))
        whenever(repository.getRetosFlow()).thenReturn(flowOf(list))
        viewModel = RetosViewModel(repository, firebaseAuth)
        testScheduler.advanceUntilIdle()

        val reto = list[0]

        // When
        viewModel.eliminarReto(reto)
        testScheduler.advanceUntilIdle()

        // Then
        verify(repository).deleteReto(reto.id)
    }
}
