package com.example.pb.repository

import kotlin.random.Random

class PokemonRepository {

    // Construye URL del sprite directo — 151 Pokémon del pokedex original
    // Fuente: PokeAPI sprites en GitHub (permite requests desde apps Android)
    suspend fun getRandomPokemonImageUrl(): String {
        val id = Random.nextInt(1, 152)
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    }
}
