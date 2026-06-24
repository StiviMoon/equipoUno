package com.example.pb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pb.repository.PokemonRepository
import com.example.pb.repository.PokemonData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    sealed class PokemonState {
        object Idle : PokemonState()
        object Loading : PokemonState()
        data class Success(val pokemon: PokemonData) : PokemonState()
        data class Error(val error: Throwable) : PokemonState()
    }

    private val _pokemonState = MutableStateFlow<PokemonState>(PokemonState.Idle)
    val pokemonState: StateFlow<PokemonState> = _pokemonState.asStateFlow()

    fun loadRandomPokemon() {
        _pokemonState.value = PokemonState.Loading
        viewModelScope.launch {
            try {
                val pokemon = repository.getRandomPokemon()
                _pokemonState.value = PokemonState.Success(pokemon)
            } catch (e: Exception) {
                _pokemonState.value = PokemonState.Error(e)
            }
        }
    }

    fun resetState() {
        _pokemonState.value = PokemonState.Idle
    }
}
