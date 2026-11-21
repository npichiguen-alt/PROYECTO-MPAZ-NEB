package com.example.mpazneb.estudiante

import com.example.mpazneb.data.local.RespuestaDao
import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.model.RespuestaRequest
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.estudiante.RespuestaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RespuestaRepositoryTest {

    @Test
    fun guardarRespuestaYResultadoExitoso() = runTest {
        val dao = mockk<RespuestaDao>(relaxed = true)
        val api = mockk<MpazApi>()

        val respRemota = Respuesta(
            id = 1,
            asignacionId = 10,
            actividadId = 5,
            preguntaId = 2,
            respuestaSeleccionada = "B",
            esCorrecta = true,
            numeroIntento = 1,
            fechaRespuesta = System.currentTimeMillis()
        )

        coEvery {
            api.registrarRespuesta(
                RespuestaRequest(
                    asignacionId = 10,
                    actividadId = 5,
                    preguntaId = 2,
                    respuestaSeleccionada = "B"
                )
            )
        } returns respRemota

        coEvery { api.calcularResultado(10) } returns Resultado(id = 1, asignacionId = 10, aciertos = 1, totalPreguntas = 1, intentos = 1)

        val repo = RespuestaRepository(dao, api)
        val resultado = repo.guardarRespuestaYResultado(
            asignacionId = 10,
            actividadId = 5,
            preguntaId = 2,
            respuestaSeleccionada = "B",
            esCorrecta = true,
            preguntasIds = listOf(2)
        )

        assertTrue(resultado)
    }

    @Test
    fun guardarRespuestaYResultadoRechazadoPorServidor409() = runTest {
        val dao = mockk<RespuestaDao>(relaxed = true)
        val api = mockk<MpazApi>()

        val errorResponse = Response.error<Respuesta>(
            409,
            "{\"mensaje\":\"Conflicto\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery {
            api.registrarRespuesta(any())
        } throws HttpException(errorResponse)

        coEvery { api.obtenerRespuestasPorAsignacion(10) } returns emptyList()

        val repo = RespuestaRepository(dao, api)
        val resultado = repo.guardarRespuestaYResultado(
            asignacionId = 10,
            actividadId = 5,
            preguntaId = 2,
            respuestaSeleccionada = "B",
            esCorrecta = true,
            preguntasIds = listOf(2)
        )

        assertFalse(resultado)
    }

    @Test
    fun obtenerRespuestasParaMostrarConsultaServidorExitosamente() = runTest {
        val dao = mockk<RespuestaDao>(relaxed = true)
        val api = mockk<MpazApi>()

        val lista = listOf(
            Respuesta(id = 1, asignacionId = 10, actividadId = 5, preguntaId = 1, respuestaSeleccionada = "A", esCorrecta = true, numeroIntento = 1, fechaRespuesta = 0)
        )

        coEvery { api.obtenerRespuestasPorAsignacion(10) } returns lista
        coEvery { dao.obtenerRespuestas(10) } returns emptyList()

        val repo = RespuestaRepository(dao, api)
        val resultado = repo.obtenerRespuestasParaMostrar(10)

        assertEquals(1, resultado.size)
        assertEquals("A", resultado[0].respuestaSeleccionada)
    }
}
