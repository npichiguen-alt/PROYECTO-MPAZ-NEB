
package com.example.mpazneb.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

data class PokemonDto(
    val name: String,
    val sprites: PokemonSprites
)

data class PokemonSprites(
    @SerializedName("front_default")
    val imagen: String?
)

interface PokeApiService {

    @GET("pokemon/{nombre}/")
    suspend fun obtenerPokemon(
        @Path("nombre") nombre: String
    ): PokemonDto
}

object PokeApiClient {

    val api: PokeApiService by lazy {

        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(PokeApiService::class.java)
    }
}
