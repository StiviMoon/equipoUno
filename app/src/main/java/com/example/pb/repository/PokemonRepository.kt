package com.example.pb.repository

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class PokemonRepository @Inject constructor() {

    // Construye URL del sprite directo — 151 Pokémon del pokedex original
    // Fuente: PokeAPI sprites en GitHub (permite requests desde apps Android)
    suspend fun getRandomPokemonImageUrl(): String {
        val id = Random.nextInt(1, 152)
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    }
}
