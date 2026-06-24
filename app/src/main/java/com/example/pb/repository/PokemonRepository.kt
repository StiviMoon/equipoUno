package com.example.pb.repository

import com.example.pb.data.api.PokemonApiService
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

data class PokemonData(
    val name: String,
    val imageUrl: String
)

@Singleton
class PokemonRepository @Inject constructor(
    private val apiService: PokemonApiService
) {
    suspend fun getRandomPokemon(): PokemonData {
        val id = Random.nextInt(1, 152)
        val response = apiService.getPokemon(id)
        return PokemonData(
            name = response.name.replaceFirstChar { it.uppercase() },
            imageUrl = response.sprites.front_default
        )
    }
}
