package com.example.pb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pb.model.Reto
import com.example.pb.repository.RetoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * RetosViewModel — Contiene la lógica de negocio para gestionar los retos.
 *
 * Sigue el patrón MVVM: actúa como intermediario entre la Vista (RetosFragment)
 * y los datos (RetoRepository). La Vista NUNCA toca la base de datos directamente.
 *
 * Expone el estado de la UI mediante un [StateFlow] que el Fragment observa
 * de forma reactiva. Cuando los datos cambian, el Fragment se actualiza solo.
 *
 * @param repository Fuente de datos que abstrae las operaciones de Room.
 */
class RetosViewModel(private val repository: RetoRepository) : ViewModel() {

    // _uiState es privado y mutable: solo el ViewModel puede cambiar su valor
    private val _uiState = MutableStateFlow<RetosUiState>(RetosUiState.Loading)

    // uiState es público y de solo lectura: el Fragment solo puede observarlo, no modificarlo
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    // Al crear el ViewModel, empieza a escuchar los cambios de la base de datos
    init {
        cargarRetos()
    }

    /**
     * Observa continuamente la lista de retos desde Room.
     *
     * [RetoRepository.getAllRetos] devuelve un [Flow] reactivo: cada vez que
     * se inserta, edita o borra un reto en SQLite, Room emite la nueva lista
     * automáticamente aquí, y se actualiza el [_uiState] sin necesidad de
     * recargar nada manualmente.
     */
    private fun cargarRetos() {
        viewModelScope.launch {
            repository.getAllRetos().collect { lista ->
                // Si la lista está vacía → estado Empty, si tiene datos → estado Success
                _uiState.value = if (lista.isEmpty()) RetosUiState.Empty
                                 else RetosUiState.Success(lista)
            }
        }
    }

    /**
     * Inserta un nuevo reto en la base de datos.
     * Se ejecuta en una Corrutina para no bloquear el hilo principal (UI).
     * @param descripcion El texto del nuevo reto a guardar.
     */
    fun insertarReto(descripcion: String) {
        viewModelScope.launch {
            repository.insertReto(Reto(descripcion = descripcion))
        }
    }

    /**
     * Actualiza la descripción de un reto existente en la base de datos.
     * Se ejecuta en una Corrutina (hilo secundario).
     * @param reto El objeto Reto con el id existente y la nueva descripción.
     */
    fun editarReto(reto: Reto) {
        viewModelScope.launch { repository.updateReto(reto) }
    }

    /**
     * Elimina un reto de la base de datos. (HU 9.0)
     *
     * Llamado desde [RetosFragment.confirmarEliminar] cuando el usuario
     * confirma la eliminación en el cuadro de diálogo (botón "SI").
     *
     * Se ejecuta en [viewModelScope] con una Corrutina: esto asegura que
     * el DELETE en SQLite corra en un hilo secundario y no congele la pantalla.
     * Al completarse, Room notificará al Flow de [cargarRetos] y la lista
     * se actualizará automáticamente en el RecyclerView.
     *
     * @param reto El objeto Reto que se desea borrar.
     */
    fun eliminarReto(reto: Reto) {
        viewModelScope.launch { repository.deleteReto(reto) }
    }
}

/**
 * Fábrica necesaria para crear [RetosViewModel] con parámetros personalizados.
 * Android no permite crear ViewModels con constructores con parámetros directamente,
 * por eso se usa este Factory.
 */
class RetosViewModelFactory(private val repository: RetoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RetosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}

/**
 * Clase sellada que representa los posibles estados de la pantalla de retos.
 *
 * - [Loading]: estado inicial, mientras Room lee los datos por primera vez.
 * - [Empty]: la tabla de retos está vacía → se muestra el texto "No hay retos".
 * - [Success]: hay retos disponibles → se muestra la lista en el RecyclerView.
 */
sealed class RetosUiState {
    object Loading : RetosUiState()
    object Empty   : RetosUiState()
    data class Success(val retos: List<Reto>) : RetosUiState()
}