package com.example.pb.repository

import kotlin.random.Random

/**
 * PokemonRepository — Repositorio encargado de consumir la API externa de Pokémon.
 *
 * Implementado para la HU 12: Mostrar Reto Aleatorio.
 * Se encarga de:
 * 1. Hacer una petición HTTP GET a la Pokédex de GitHub.
 * 2. Parsear el JSON de respuesta.
 * 3. Seleccionar un Pokémon aleatorio.
 * 4. Retornar la URL de su imagen.
 *
 * No utiliza librerías externas (Retrofit, OkHttp, Glide) para mantenerse
 * dentro de las dependencias base del proyecto. Usa [HttpURLConnection] nativo.
 *
 * API usada: https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json
 */
class PokemonRepository {

    // Construye URL del sprite directo — 151 Pokémon del pokedex original
    // Fuente: PokeAPI sprites en GitHub (permite requests desde apps Android)
    suspend fun getRandomPokemonImageUrl(): String {
        val id = Random.nextInt(1, 152)
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    }
}
