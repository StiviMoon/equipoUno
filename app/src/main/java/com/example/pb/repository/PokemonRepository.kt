package com.example.pb.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

class PokemonRepository {

    suspend fun getRandomPokemonImageUrl(): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(responseText)
                val pokemonArray = jsonObject.getJSONArray("pokemon")
                
                if (pokemonArray.length() > 0) {
                    val randomIndex = Random.nextInt(pokemonArray.length())
                    val randomPokemon = pokemonArray.getJSONObject(randomIndex)
                    var imageUrl = randomPokemon.getString("img")
                    
                    // The API returns http://, replace with https:// to avoid cleartext issues on Android
                    if (imageUrl.startsWith("http://")) {
                        imageUrl = imageUrl.replace("http://", "https://")
                    }
                    return@withContext imageUrl
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}
