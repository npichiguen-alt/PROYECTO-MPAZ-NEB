package com.example.mpazneb.estudiante

import com.example.mpazneb.data.remote.PokemonDto
import com.example.mpazneb.data.remote.PokemonSprites
import com.example.mpazneb.repository.pokemon.PokemonRepository
import com.example.mpazneb.viewmodel.PokemonViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.unmockkObject
import kotlinx.coroutines.CancellationException
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

    @Test
    fun respuestaConImagenNulaMuestraError() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites(null))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertTrue(viewModel.error)
        assertNull(viewModel.imagenUrl)
    }

    @Test
    fun respuestaConUrlVaciaMuestraError() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("   "))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertTrue(viewModel.error)
        assertNull(viewModel.imagenUrl)
    }

    @Test
    fun respuestaConSpritesNulosMuestraError() = runTest {
        val dto = PokemonDto("pikachu", null)
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertTrue(viewModel.error)
        assertNull(viewModel.imagenUrl)
    }

    @Test
    fun reintentoDespuesDeErrorPermiteNuevaCarga() = runTest {
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } throws IOException("Fallo de red")

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertTrue(viewModel.error)

        val dtoExitoso = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dtoExitoso

        viewModel.reintentar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertFalse(viewModel.error)
        assertEquals("https://pokeapi.co/pikachu.png", viewModel.imagenUrl)
    }

    @Test
    fun prevencionDeConsultasDuplicadasMientrasCargaOMuestraImagen() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        coVerify(exactly = 1) { PokemonRepository.obtenerPokemon("pikachu") }
    }

    @Test
    fun finalizacionCorrectaDelEstadoDeCargaEnExitoYEnError() = runTest {
        val dto = PokemonDto("pikachu", PokemonSprites("https://pokeapi.co/pikachu.png"))
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } returns dto

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()
        assertFalse(viewModel.cargando)

        coEvery { PokemonRepository.obtenerPokemon("eevee") } throws IOException("Fallo")
        viewModel.cargar("eevee")
        advanceUntilIdle()
        assertFalse(viewModel.cargando)
    }

    @Test
    fun manejoDeCancelacionDeCorrutinasRelanzaCancellationException() = runTest {
        coEvery { PokemonRepository.obtenerPokemon("pikachu") } throws CancellationException("Cancelado")

        val viewModel = PokemonViewModel()
        viewModel.cargar("pikachu")
        advanceUntilIdle()

        assertFalse(viewModel.cargando)
        assertFalse(viewModel.error)
        assertNull(viewModel.imagenUrl)
    }
}
