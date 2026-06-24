package com.example.pb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pb.data.model.Reto
import com.example.pb.data.repository.RetosRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * RetosViewModel — Contiene la lógica de negocio para gestionar los retos en Firebase Firestore.
 *
 * Sigue el patrón MVVM: actúa como intermediario entre la Vista (RetosFragment)
 * y los datos (RetosRepository).
 *
 * Expone el estado de la UI mediante un [StateFlow] que el Fragment observa
 * de forma reactiva.
 */
class RetosViewModel(private val repository: RetosRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RetosUiState>(RetosUiState.Loading)
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        cargarRetos()
    }

    /**
     * Carga y se suscribe al flujo de retos en Firestore.
     * Maneja los estados Loading, Empty, Success y Error.
     */
    private fun cargarRetos() {
        _uiState.value = RetosUiState.Loading
        viewModelScope.launch {
            repository.getRetosFlow()
                .catch { exception ->
                    Log.e("RetosViewModel", "Error obteniendo retos desde Firestore: ${exception.message}", exception)
                    _uiState.value = RetosUiState.Error(exception)
                }
                .collect { lista ->
                    _uiState.value = if (lista.isEmpty()) {
                        RetosUiState.Empty
                    } else {
                        RetosUiState.Success(lista)
                    }
                }
        }
    }

    /**
     * Inserta un nuevo reto en Firestore con el UID del usuario logueado.
     */
    fun insertarReto(descripcion: String) {
        viewModelScope.launch {
            try {
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val nuevoReto = Reto(
                    descripcion = descripcion,
                    uidUsuario = currentUid,
                    fechaCreacion = System.currentTimeMillis()
                )
                repository.insertReto(nuevoReto)
            } catch (e: Exception) {
                Log.e("RetosViewModel", "Error insertando reto en Firestore: ${e.message}", e)
            }
        }
    }

    /**
     * Actualiza la descripción de un reto existente.
     */
    fun editarReto(reto: Reto) {
        viewModelScope.launch {
            try {
                repository.updateReto(reto)
            } catch (e: Exception) {
                Log.e("RetosViewModel", "Error editando reto en Firestore: ${e.message}", e)
            }
        }
    }

    /**
     * Elimina un reto de la base de datos Firestore por su ID.
     */
    fun eliminarReto(reto: Reto) {
        viewModelScope.launch {
            try {
                repository.deleteReto(reto.id)
            } catch (e: Exception) {
                Log.e("RetosViewModel", "Error eliminando reto en Firestore: ${e.message}", e)
            }
        }
    }
}

/**
 * Fábrica para instanciar [RetosViewModel] pasándole el repositorio.
 */
class RetosViewModelFactory(private val repository: RetosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RetosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}

/**
 * Estados de la UI para la pantalla de gestión de retos.
 */
sealed class RetosUiState {
    object Loading : RetosUiState()
    object Empty : RetosUiState()
    data class Success(val retos: List<Reto>) : RetosUiState()
    data class Error(val exception: Throwable) : RetosUiState()
}