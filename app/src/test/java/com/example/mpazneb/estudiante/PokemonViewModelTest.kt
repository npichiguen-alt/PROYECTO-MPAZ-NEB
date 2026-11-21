package com.example.mpazneb.estudiante

import com.example.mpazneb.data.remote.PokemonDto
import com.example.mpazneb.data.remote.PokemonSprites
import com.example.mpazneb.repository.pokemon.PokemonRepository
import com.example.mpazneb.viewmodel.PokemonViewModel
import io.mockk.coEvery
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModelTest {

    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
        mockkObject(PokemonRepository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        unmockkObject(PokemonRepository)
        unmockkAll()
    }

    @Test
    fun estadoInicialEsCorrecto() {
        val viewModel = PokemonViewModel()
        assertNull(viewModel.imagenUrl)
        assertFalse(viewModel.cargando)
        assertFalse(viewModel.error)
    }

    @Test
    fun cargaExitosaActualizaImagenUrl() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertFalse(viewModel.error)
        assertEquals("https://pokeapi.co/pikachu.png", viewModel.imagenUrl)
    }

    @Test
    fun estadoTrasConsultaFallaRedMuestraErrorSinDetenerFlujo() = runTest {
        coEvery { PokemonRepository.obtenerPokemon("error-pokemon") } throws IOException("Sin conexión")

        val viewModel = PokemonViewModel()
        viewModel.cargar("error-pokemon")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertTrue(viewModel.error)
        assertNull(viewModel.imagenUrl)
    }
}
