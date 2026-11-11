
package com.example.mpazneb.estudiante

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
import org.junit.Assert.assertTrue
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

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



}
