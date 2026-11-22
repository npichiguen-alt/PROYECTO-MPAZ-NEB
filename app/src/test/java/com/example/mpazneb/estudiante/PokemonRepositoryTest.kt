package com.example.mpazneb.estudiante

import com.example.mpazneb.data.remote.PokeApiClient
import com.example.mpazneb.data.remote.PokeApiService
import com.example.mpazneb.data.remote.PokemonDto
import com.example.mpazneb.data.remote.PokemonSprites
import com.example.mpazneb.repository.pokemon.PokemonRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepositoryTest {

    private val apiMock = mockk<PokeApiService>()

    @Before
    fun preparar() {
        PokemonRepository.limpiarCache()
        mockkObject(PokeApiClient)
        coEvery { PokeApiClient.api } returns apiMock
    }

    @After
    fun limpiar() {
        PokemonRepository.limpiarCache()
        unmockkObject(PokeApiClient)
        unmockkAll()
    }

    @Test
    fun obtenerPokemonAlmacenaEnCacheSiUrlEsValida() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { apiMock.obtenerPokemon("pikachu") } returns dto

        val resultado1 = PokemonRepository.obtenerPokemon("pikachu")
        assertEquals("https://pokeapi.co/pikachu.png", resultado1.sprites?.imagen)
        assertTrue(PokemonRepository.estaEnCache("pikachu"))

        val resultado2 = PokemonRepository.obtenerPokemon("pikachu")
        assertEquals("https://pokeapi.co/pikachu.png", resultado2.sprites?.imagen)

        coVerify(exactly = 1) { apiMock.obtenerPokemon("pikachu") }
    }

    @Test
    fun obtenerPokemonNoAlmacenaEnCacheSiUrlEsInvalida() = runTest {
        val dtoInvalido = PokemonDto("pikachu", PokemonSprites(null))
        coEvery { apiMock.obtenerPokemon("pikachu") } returns dtoInvalido

        val resultado = PokemonRepository.obtenerPokemon("pikachu")
        assertNull(resultado.sprites?.imagen)
        assertFalse(PokemonRepository.estaEnCache("pikachu"))

        val dtoValido = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { apiMock.obtenerPokemon("pikachu") } returns dtoValido

        val resultado2 = PokemonRepository.obtenerPokemon("pikachu")
        assertEquals("https://pokeapi.co/pikachu.png", resultado2.sprites?.imagen)

        coVerify(exactly = 2) { apiMock.obtenerPokemon("pikachu") }
    }

    @Test
    fun limpiarCacheEliminaElementoEspecifico() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { apiMock.obtenerPokemon("pikachu") } returns dto

        PokemonRepository.obtenerPokemon("pikachu")
        assertTrue(PokemonRepository.estaEnCache("pikachu"))

        PokemonRepository.limpiarCache("pikachu")
        assertFalse(PokemonRepository.estaEnCache("pikachu"))
    }
}
