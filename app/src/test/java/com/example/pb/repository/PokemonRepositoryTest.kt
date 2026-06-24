package com.example.pb.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonRepositoryTest {

    private val repository = PokemonRepository()

    @Test
    fun testGetRandomPokemonImageUrl() = runBlocking {
        // When
        val url = repository.getRandomPokemonImageUrl()

        // Then
        assertNotNull("The URL should not be null", url)
        assertTrue(
            "The URL should point to raw.githubusercontent.com for PokeAPI sprites",
            url.startsWith("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/")
        )
        assertTrue(
            "The URL should end with .png extension",
            url.endsWith(".png")
        )

        // Extract ID and check it is within range 1 to 151
        val regex = """.*/pokemon/(\d+)\.png""".toRegex()
        val matchResult = regex.find(url)
        assertNotNull("The URL should match the expected pattern to extract the ID", matchResult)
        
        val idString = matchResult!!.groupValues[1]
        val id = idString.toIntOrNull()
        assertNotNull("The extracted ID should be a valid integer", id)
        assertTrue("The Pokémon ID should be between 1 and 151", id!! in 1..151)
    }
}
