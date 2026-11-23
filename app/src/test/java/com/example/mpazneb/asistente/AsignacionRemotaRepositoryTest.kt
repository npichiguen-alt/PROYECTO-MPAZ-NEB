package com.example.mpazneb.asistente

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.AsignacionRequest
import com.example.mpazneb.repository.asistente.AsignacionRemotaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsignacionRemotaRepositoryTest {

    @Test
    fun obtenerActividadesFiltraInactivasYNormaliza() = runTest {
        val api = mockk<MpazApi>()
        val repository = AsignacionRemotaRepository(api)

        coEvery { api.obtenerActividades() } returns listOf(
            Actividad(
                id = 1,
                titulo = "Matemática Básica",
                modulo = "matematicas",
                curso = 2,
                dificultad = "facil",
                descripcion = "Suma y resta",
                activa = true
            ),
            Actividad(
                id = 2,
                titulo = "Inactiva",
                modulo = "lectura",
                curso = 1,
                dificultad = "media",
                descripcion = "Inactiva",
                activa = false
            )
        )

        val resultado = repository.obtenerActividades()

        assertEquals(1, resultado.size)
        assertEquals(1, resultado[0].id)
        assertEquals("Matemática", resultado[0].modulo)
        assertEquals("Básica", resultado[0].dificultad)
        assertEquals("Disponible", resultado[0].estado)
    }

    @Test
    fun crearAsignacionRemotaRetornaDatosDelBackend() = runTest {
        val api = mockk<MpazApi>()
        val repository = AsignacionRemotaRepository(api)

        val solicitud = AsignacionRequest(
            actividadId = 1,
            actividadTitulo = "Prueba",
            estudianteAlias = "Estudiante A",
            asistenteAlias = "ASISTENTE_DEMO",
            curso = 2
        )

        val respuestaEsperada = AsignacionAula(
            id = 100,
            actividadId = 1,
            actividadTitulo = "Prueba",
            estudianteAlias = "Estudiante A",
            codigoTemporal = "AWS-99999",
            asistenteAlias = "ASISTENTE_DEMO",
            curso = 2,
            estado = "Pendiente"
        )

        coEvery { api.crearAsignacionAsistente(solicitud) } returns respuestaEsperada

        val resultado = repository.crearAsignacion(solicitud)

        assertEquals("AWS-99999", resultado.codigoTemporal)
        assertEquals(100, resultado.id)
        assertEquals("Pendiente", resultado.estado)
    }
}
