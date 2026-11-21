package com.example.mpazneb.estudiante

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.AsignacionRemota
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.asignacion.AsignacionRemotaRepository
import com.example.mpazneb.repository.estudiante.EstudianteRemotoRepository
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
class EstudianteRemotoRepositoryTest {

    @Test
    fun buscarAsignacionPorCodigoExitosoRetornaAsignacion() = runTest {
        val asignacionRepo = mockk<AsignacionRemotaRepository>()
        val api = mockk<MpazApi>()

        val remota = AsignacionRemota(
            id = 1,
            actividadId = 10,
            actividadTitulo = "Matemáticas",
            estudianteAlias = "Juan",
            codigoTemporal = "COD123",
            asistenteAlias = "Asistente1",
            curso = 1,
            estado = "Pendiente",
            fechaAsignacion = System.currentTimeMillis()
        )

        coEvery { asignacionRepo.obtenerAsignacionPorCodigo("COD123") } returns remota

        val repository = EstudianteRemotoRepository(asignacionRepo, api)
        val resultado = repository.buscarAsignacionPorCodigo("COD123")

        assertEquals(1, resultado?.id)
        assertEquals(10, resultado?.actividadId)
        assertEquals("Juan", resultado?.estudianteFicticio)
    }

    @Test
    fun buscarAsignacionPorCodigoHttp404RetornaNull() = runTest {
        val asignacionRepo = mockk<AsignacionRemotaRepository>()
        val api = mockk<MpazApi>()

        val errorResponse = Response.error<Any>(
            404,
            "{\"mensaje\":\"Not found\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery { asignacionRepo.obtenerAsignacionPorCodigo("404-CODE") } throws HttpException(errorResponse)

        val repository = EstudianteRemotoRepository(asignacionRepo, api)
        val resultado = repository.buscarAsignacionPorCodigo("404-CODE")

        assertNull(resultado)
    }

    @Test(expected = IOException::class)
    fun buscarAsignacionPorCodigoErrorDeRedLanzaIOException() = runTest {
        val asignacionRepo = mockk<AsignacionRemotaRepository>()
        val api = mockk<MpazApi>()

        coEvery { asignacionRepo.obtenerAsignacionPorCodigo("NET-FAIL") } throws IOException("No internet")

        val repository = EstudianteRemotoRepository(asignacionRepo, api)
        repository.buscarAsignacionPorCodigo("NET-FAIL")
    }

    @Test
    fun obtenerResultadosExitosoRetornaLista() = runTest {
        val asignacionRepo = mockk<AsignacionRemotaRepository>()
        val api = mockk<MpazApi>()

        val res = listOf(
            Resultado(id = 1, asignacionId = 5, aciertos = 2, totalPreguntas = 2, intentos = 2, observacion = "Excelente")
        )

        coEvery { api.obtenerResultadosPorAsignacion(5) } returns res

        val repository = EstudianteRemotoRepository(asignacionRepo, api)
        val lista = repository.obtenerResultados(5)

        assertEquals(1, lista.size)
        assertEquals(2, lista[0].aciertos)
    }

    @Test
    fun obtenerResultados404RetornaListaVacia() = runTest {
        val asignacionRepo = mockk<AsignacionRemotaRepository>()
        val api = mockk<MpazApi>()

        val errorResponse = Response.error<Any>(
            404,
            "{\"mensaje\":\"Sin resultados\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery { api.obtenerResultadosPorAsignacion(99) } throws HttpException(errorResponse)

        val repository = EstudianteRemotoRepository(asignacionRepo, api)
        val lista = repository.obtenerResultados(99)

        assertEquals(0, lista.size)
    }
}
