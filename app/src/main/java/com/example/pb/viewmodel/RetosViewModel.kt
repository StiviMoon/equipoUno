package com.example.pb.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pb.data.AppDatabase
import com.example.pb.model.Reto
import com.example.pb.repository.RetoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RetosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: RetoRepository

    val allRetos: Flow<List<Reto>>

    init {
        val dao = AppDatabase.getInstance(application).retoDao()
        repository = RetoRepository(dao)
        allRetos = repository.allRetos
    }

    fun insert(reto: Reto) = viewModelScope.launch {
        repository.insert(reto)
    }

    fun update(reto: Reto) = viewModelScope.launch {
        repository.update(reto)
    }

    fun delete(reto: Reto) = viewModelScope.launch {
        repository.delete(reto)
    }
}
