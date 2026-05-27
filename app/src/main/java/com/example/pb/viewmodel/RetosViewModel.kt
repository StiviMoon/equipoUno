package com.example.pb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import  com.example.pb.model.Reto
import com.example.pb.repository.RetoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class RetosViewModel (private val repository: RetoRepository): ViewModel(){

    private val _uiState = MutableStateFlow<RetosUiState>(RetosUiState.Loading)
    val uiState: StateFlow<RetosUiState> = _uiState.asStateFlow()

    init {
        cargarRetos()
    }

    private fun cargarRetos(){
        viewModelScope.launch {
            repository.getAllRetos().collect { lista ->
                _uiState.value = if(lista.isEmpty())RetosUiState.Empty
                                    else RetosUiState.Success(lista)
            }
        }
    }
    fun insertarReto(descripcion:String){
        viewModelScope.launch {
            repository.insertReto(Reto(descripcion = descripcion))
        }
    }

    fun editarReto(reto: Reto){
        viewModelScope.launch { repository.updateReto(reto) }
    }
    fun eliminarReto(reto: Reto){
        viewModelScope.launch { repository.deleteReto(reto) }
    }
}

class RetosViewModelFactory(private val repository: RetoRepository) : ViewModelProvider.Factory{
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(RetosViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return RetosViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}



sealed class RetosUiState {
    object Loading : RetosUiState()
    object Empty   : RetosUiState()
    data class Success(val retos: List<Reto>) : RetosUiState()
}