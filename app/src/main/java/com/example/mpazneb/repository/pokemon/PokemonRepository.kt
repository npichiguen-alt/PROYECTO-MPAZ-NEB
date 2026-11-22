package com.example.mpazneb.repository.pokemon

import com.example.mpazneb.data.remote.PokeApiClient
import com.example.mpazneb.data.remote.PokemonDto

object PokemonRepository {

    private val cache = mutableMapOf<String, PokemonDto>()

    suspend fun obtenerPokemon(nombre: String): PokemonDto {
        val clave = nombre.lowercase().trim()

        cache[clave]?.let { cachedDto ->
            val img = cachedDto.sprites?.imagen
            if (!img.isNullOrBlank()) {
                return cachedDto
            } else {
                cache.remove(clave)
            }
        }

        val pokemon = PokeApiClient.api.obtenerPokemon(clave)

        if (!pokemon.sprites?.imagen.isNullOrBlank()) {
            cache[clave] = pokemon
        }

        return pokemon
    }

    fun limpiarCache(nombre: String? = null) {
        if (nombre != null) {
            cache.remove(nombre.lowercase().trim())
        } else {
            cache.clear()
        }
    }

    fun estaEnCache(nombre: String): Boolean {
        val clave = nombre.lowercase().trim()
        val cached = cache[clave]
        return cached != null && !cached.sprites?.imagen.isNullOrBlank()
    }
}
