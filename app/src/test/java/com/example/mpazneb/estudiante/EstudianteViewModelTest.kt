
package com.example.mpazneb.estudiante

import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.repository.estudiante.EstudianteRepository
import com.example.mpazneb.viewmodel.EstudianteViewModel

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.repository.estudiante.ContenidoEstudianteRepository
import com.example.mpazneb.repository.estudiante.RespuestaRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EstudianteViewModelTest {

    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun codigoVacioMuestraMensajeDeError() {
        val viewModel = EstudianteViewModel(
            repositorio = mockk<EstudianteRepository>()
        )

        viewModel.actualizarCodigo("")
        viewModel.intentarIngresar()

        assertEquals(
            "Debes ingresar un código.",
            viewModel.estado.value.mensaje
        )
        assertFalse(viewModel.estado.value.ingresoValidado)
    }

    @Test
    fun codigoConSoloEspaciosNoPermiteIngresar() {
        val viewModel = EstudianteViewModel(
            repositorio = mockk<EstudianteRepository>()
        )

        viewModel.actualizarCodigo("   ")
        viewModel.intentarIngresar()

        assertEquals(
            "Debes ingresar un código.",
            viewModel.estado.value.mensaje
        )
        assertFalse(viewModel.estado.value.ingresoValidado)
    }

    @Test
    fun codigoInexistenteNoPermiteIngresar() = runTest {

        val repositorio = mockk<EstudianteRepository>()

        coEvery {
            repositorio.buscarAsignacionPorCodigo("NO-EXISTE")
        } returns null

        val viewModel = EstudianteViewModel(
            repositorio = repositorio
        )

        viewModel.actualizarCodigo("NO-EXISTE")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertEquals(
            "Código no encontrado.",
            viewModel.estado.value.mensaje
        )

        assertFalse(
            viewModel.estado.value.ingresoValidado
        )

        coVerify(exactly = 1) {
            repositorio.buscarAsignacionPorCodigo("NO-EXISTE")
        }
    }

    @Test
    fun codigoValidoPermiteIngresar() = runTest {

        val repositorio = mockk<EstudianteRepository>()

        val asignacion = Asignacion(
            id = 1,
            actividadId = 1,
            estudianteFicticio = "Estudiante de prueba",
            codigoTemporal = "EST-123",
            estado = "Pendiente"
        )

        coEvery {
            repositorio.buscarAsignacionPorCodigo("EST-123")
        } returns asignacion

        coEvery {
            repositorio.obtenerResultados(1)
        } returns emptyList()

        val viewModel = EstudianteViewModel(
            repositorio = repositorio
        )

        viewModel.actualizarCodigo("EST-123")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertTrue(
            viewModel.estado.value.ingresoValidado
        )

        assertEquals(
            "Código válido. Asignación encontrada.",
            viewModel.estado.value.mensaje
        )

        assertEquals(
            1,
            viewModel.estado.value.asignacionActiva?.id
        )
    }

    @Test
    fun codigoRealConEspaciosPermiteIngresar() = runTest {

        val repositorio = mockk<EstudianteRepository>()

        val asignacion = Asignacion(
            id = 10,
            actividadId = 5,
            estudianteFicticio = "Estudiante A",
            codigoTemporal = "REAL-001",
            estado = "Pendiente"
        )

        coEvery {
            repositorio.buscarAsignacionPorCodigo("REAL-001")
        } returns asignacion

        coEvery {
            repositorio.obtenerResultados(10)
        } returns emptyList()

        val viewModel = EstudianteViewModel(
            repositorio = repositorio
        )

        viewModel.actualizarCodigo("  REAL-001  ")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertTrue(viewModel.estado.value.ingresoValidado)

        assertEquals(
            10,
            viewModel.estado.value.asignacionActiva?.id
        )

        coVerify(exactly = 1) {
            repositorio.buscarAsignacionPorCodigo("REAL-001")
        }
    }

    @Test
    fun responderPreguntaBloqueaPulsacionesMultiplesCuandoEstaGuardando() = runTest {
        val repositorio = mockk<EstudianteRepository>()
        val respuestaRepo = mockk<RespuestaRepository>()

        val asignacion = Asignacion(
            id = 1,
            actividadId = 1,
            estudianteFicticio = "Estudiante A",
            codigoTemporal = "EST-100",
            estado = "Pendiente"
        )

        coEvery { repositorio.buscarAsignacionPorCodigo("EST-100") } returns asignacion
        coEvery { repositorio.obtenerResultados(1) } returns emptyList()
        coEvery { respuestaRepo.obtenerRespuestasParaMostrar(1) } returns emptyList()

        val viewModel = EstudianteViewModel(
            repositorio = repositorio,
            respuestaRepository = respuestaRepo
        )

        viewModel.actualizarCodigo("EST-100")
        viewModel.intentarIngresar()
        advanceUntilIdle()

        // Simular intento de respuesta cuando la actividad no coincide o está vacía
        viewModel.responderPreguntaAsignada(1, "A")
        assertFalse(viewModel.guardandoRespuestaAsignada.value)
    }

    @Test
    fun errorDeConexionAlIngresarMuestraMensajeComprensible() = runTest {
        val repositorio = mockk<EstudianteRepository>()

        coEvery {
            repositorio.buscarAsignacionPorCodigo("FALLO-RED")
        } throws IOException("Conexión rechazada")

        val viewModel = EstudianteViewModel(
            repositorio = repositorio
        )

        viewModel.actualizarCodigo("FALLO-RED")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertFalse(viewModel.estado.value.ingresoValidado)
        assertEquals(
            "No se pudo conectar con el servidor. Comprueba tu conexión o inténtalo más tarde.",
            viewModel.estado.value.mensaje
        )
    }

    @Test
    fun errorHttpServidorAlIngresarMuestraMensajeComprensible() = runTest {
        val repositorio = mockk<EstudianteRepository>()

        val response = Response.error<Any>(
            500,
            "{\"error\":\"Server Error\"}".toResponseBody("application/json".toMediaType())
        )

        coEvery {
            repositorio.buscarAsignacionPorCodigo("ERROR-500")
        } throws HttpException(response)

        val viewModel = EstudianteViewModel(
            repositorio = repositorio
        )

        viewModel.actualizarCodigo("ERROR-500")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertFalse(viewModel.estado.value.ingresoValidado)
        assertEquals(
            "Error en el servidor. Inténtalo más tarde.",
            viewModel.estado.value.mensaje
        )
    }

    @Test
    fun actividadInexistenteEnServidorImpideIngreso() = runTest {
        val repositorio = mockk<EstudianteRepository>()
        val contenidoRepo = mockk<ContenidoEstudianteRepository>()

        val asignacion = Asignacion(
            id = 1,
            actividadId = 99,
            estudianteFicticio = "Estudiante B",
            codigoTemporal = "EST-99",
            estado = "Pendiente"
        )

        coEvery { repositorio.buscarAsignacionPorCodigo("EST-99") } returns asignacion
        coEvery { contenidoRepo.buscarActividad(99) } returns null

        val viewModel = EstudianteViewModel(
            repositorio = repositorio,
            contenidoRepository = contenidoRepo
        )

        viewModel.actualizarCodigo("EST-99")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertFalse(viewModel.estado.value.ingresoValidado)
        assertEquals(
            "La asignación existe, pero su actividad no está disponible en el servidor.",
            viewModel.estado.value.mensaje
        )
    }

    @Test
    fun actividadExistenteConContenidoRepoPermiteIngreso() = runTest {
        val repositorio = mockk<EstudianteRepository>()
        val contenidoRepo = mockk<ContenidoEstudianteRepository>()

        val asignacion = Asignacion(
            id = 2,
            actividadId = 10,
            estudianteFicticio = "Estudiante C",
            codigoTemporal = "EST-10",
            estado = "Pendiente"
        )

        val actividad = ActividadEntity(
            id = 10,
            titulo = "Matemáticas",
            modulo = "Módulo 1",
            curso = 1,
            dificultad = "Fácil",
            descripcion = "Descripción",
            activa = true,
            objetivo = "Objetivo",
            instrucciones = "Instrucciones"
        )

        coEvery { repositorio.buscarAsignacionPorCodigo("EST-10") } returns asignacion
        coEvery { contenidoRepo.buscarActividad(10) } returns actividad
        coEvery { contenidoRepo.obtenerPreguntas(10) } returns emptyList()
        coEvery { repositorio.obtenerResultados(2) } returns emptyList()

        val viewModel = EstudianteViewModel(
            repositorio = repositorio,
            contenidoRepository = contenidoRepo
        )

        viewModel.actualizarCodigo("EST-10")
        viewModel.intentarIngresar()

        advanceUntilIdle()

        assertTrue(viewModel.estado.value.ingresoValidado)
        assertEquals(10, viewModel.actividadAsignada.value?.id)
    }
}
