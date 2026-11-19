package com.example.mpazneb.repository.pokemon

import com.example.mpazneb.data.remote.PokeApiClient
import com.example.mpazneb.data.remote.PokemonDto

object PokemonRepository {

    private val cache = mutableMapOf<String, PokemonDto>()

    suspend fun obtenerPokemon(nombre: String): PokemonDto {

        cache[nombre]?.let {
            return it
        }

        val pokemon =
            PokeApiClient.api.obtenerPokemon(nombre)

        cache[nombre] = pokemon

        return pokemon
    }
}
