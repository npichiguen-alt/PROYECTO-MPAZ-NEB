package com.example.mpazneb.estudiante

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.repository.estudiante.ContenidoEstudianteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ContenidoEstudianteRepositoryTest {

    @Test
    fun buscarActividadExitosoRetornaActividad() = runTest {
        val db = mockk<ActividadDatabase>(relaxed = true)
        val api = mockk<MpazApi>()

        val actividadRemota = Actividad(
            id = 5,
            titulo = "Lectura 1",
            modulo = "Lenguaje",
            curso = 2,
            dificultad = "Media",
            descripcion = "Comprensión lectora",
            activa = true,
            objetivo = "Aprender a leer",
            instrucciones = "Lee con atención"
        )

        coEvery { api.obtenerActividadPorId(5) } returns actividadRemota

        val repository = ContenidoEstudianteRepository(db, api)
        val resultado = repository.buscarActividad(5)

        assertEquals(5, resultado?.id)
        assertEquals("Lectura 1", resultado?.titulo)
    }

    @Test
    fun buscarActividadHttp404RetornaNull() = runTest {
        val db = mockk<ActividadDatabase>(relaxed = true)
        val api = mockk<MpazApi>()

        val errorResponse = Response.error<Actividad>(
            404,
            "{\"mensaje\":\"No encontrada\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery { api.obtenerActividadPorId(999) } throws HttpException(errorResponse)

        val repository = ContenidoEstudianteRepository(db, api)
        val resultado = repository.buscarActividad(999)

        assertNull(resultado)
    }

    @Test(expected = IOException::class)
    fun buscarActividadErrorDeRedLanzaIOException() = runTest {
        val db = mockk<ActividadDatabase>(relaxed = true)
        val api = mockk<MpazApi>()

        coEvery { api.obtenerActividadPorId(5) } throws IOException("Timeout")

        val repository = ContenidoEstudianteRepository(db, api)
        repository.buscarActividad(5)
    }

    @Test
    fun obtenerPreguntasExitosoRetornaLista() = runTest {
        val db = mockk<ActividadDatabase>(relaxed = true)
        val api = mockk<MpazApi>()

        val preguntas = listOf(
            PreguntaEntity(id = 1, actividadId = 5, enunciado = "¿Qué es?", alternativaA = "A", alternativaB = "B", alternativaC = "C", alternativaD = "D", respuestaCorrecta = "A", retroalimentacion = "Bien")
        )

        coEvery { api.obtenerPreguntasPorActividad(5) } returns preguntas

        val repository = ContenidoEstudianteRepository(db, api)
        val lista = repository.obtenerPreguntas(5)

        assertEquals(1, lista.size)
        assertEquals("¿Qué es?", lista[0].enunciado)
    }

    @Test
    fun obtenerPreguntas404RetornaListaVacia() = runTest {
        val db = mockk<ActividadDatabase>(relaxed = true)
        val api = mockk<MpazApi>()

        val errorResponse = Response.error<List<PreguntaEntity>>(
            404,
            "{\"mensaje\":\"Sin preguntas\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery { api.obtenerPreguntasPorActividad(999) } throws HttpException(errorResponse)

        val repository = ContenidoEstudianteRepository(db, api)
        val lista = repository.obtenerPreguntas(999)

        assertEquals(0, lista.size)
    }
}
