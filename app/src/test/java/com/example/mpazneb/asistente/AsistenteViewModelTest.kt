package com.example.mpazneb.asistente

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.ResultadoAula
import com.example.mpazneb.repository.asistente.AsistenteDataSource
import com.example.mpazneb.viewmodel.AsistenteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AsistenteViewModelTest {

    @Before
    fun preparar() {
        Dispatchers.setMain(StandardTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // Prepara una fuente ficticia con actividades y estudiantes para las pruebas.
    private fun crearFuente(): AsistenteDataSource {
        val fuente = mockk<AsistenteDataSource>(relaxed = true)

        every { fuente.actividades } returns MutableStateFlow(
            listOf(
                ActividadAula(
                    id = 1,
                    titulo = "Cuento del bosque",
                    modulo = "Lectura",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Comprensión de lectura",
                    estado = "Disponible"
                ),
                ActividadAula(
                    id = 5,
                    titulo = "Borrador de lectura",
                    modulo = "Lectura",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Todavía no aprobada",
                    estado = "Borrador"
                )
            )
        )

        every { fuente.estudiantes } returns MutableStateFlow(
            listOf(
                EstudianteAula(alias = "Estudiante A", activo = true),
                EstudianteAula(alias = "Estudiante D", activo = false)
            )
        )

        every { fuente.asignaciones } returns MutableStateFlow(emptyList<AsignacionAula>())
        every { fuente.resultados } returns MutableStateFlow(emptyList<ResultadoAula>())

        return fuente
    }
    // Mantiene activos los StateFlow del ViewModel durante cada prueba.
    private fun crearViewModel(
        fuente: AsistenteDataSource,
        scope: TestScope
    ): AsistenteViewModel {
        val viewModel = AsistenteViewModel(fuente)

        scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
            viewModel.actividades.collect{}
        }
        scope.backgroundScope.launch(UnconfinedTestDispatcher(scope.testScheduler)) {
            viewModel.estudiantes.collect{}
        }

        scope.runCurrent()
        return viewModel
    }

    @Test
    fun asignacionValidaSeEnviaAlRepositorio() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirActividad(1)
        viewModel.elegirEstudiante("Estudiante A")
        viewModel.asignarActividad()
        advanceUntilIdle()

        coVerify(exactly = 1) {
            fuente.crearAsignacion(match {
                it.actividadId == 1 &&
                        it.estudianteAlias == "Estudiante A" &&
                        it.curso == 2 &&
                        it.codigoTemporal.length == 10
            })
        }
        assertTrue(viewModel.mensaje.startsWith("Actividad asignada correctamente."))
        assertEquals(0, viewModel.formulario.actividadId)
        assertFalse(viewModel.trabajando)
    }

    @Test
    fun estudianteInactivoNoPuedeRecibirActividad() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirActividad(1)
        viewModel.elegirEstudiante("Estudiante D")
        viewModel.asignarActividad()

        assertEquals("El estudiante no existe o no está activo.", viewModel.mensaje)
        coVerify(exactly = 0) { fuente.crearAsignacion(any()) }
    }

    @Test
    fun actividadEnBorradorNoSePuedeAsignar() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirActividad(5)
        viewModel.elegirEstudiante("Estudiante A")
        viewModel.asignarActividad()

        assertEquals(
            "La actividad seleccionada no está disponible para esos filtros.",
            viewModel.mensaje
        )
        coVerify(exactly = 0) { fuente.crearAsignacion(any()) }
    }

    @Test
    fun formularioSinActividadNoSeGuarda() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirEstudiante("Estudiante A")
        viewModel.asignarActividad()

        assertEquals("Selecciona una actividad.", viewModel.mensaje)
        coVerify(exactly = 0) { fuente.crearAsignacion(any()) }
    }

    @Test
    fun formularioSinEstudianteNoSeGuarda() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirActividad(1)
        viewModel.asignarActividad()

        assertEquals("Selecciona un estudiante.", viewModel.mensaje)
        coVerify(exactly = 0) { fuente.crearAsignacion(any()) }
    }

    @Test
    fun observacionVaciaNoSeGuarda() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.guardarObservacion(50, "   ")

        assertEquals("Escribe una observación.", viewModel.mensaje)
        coVerify(exactly = 0) { fuente.guardarObservacion(any(), any()) }
    }

    @Test
    fun observacionDemasiadoLargaNoSeGuarda() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.guardarObservacion(50, "a".repeat(251))

        assertEquals("La observación no puede superar 250 caracteres.", viewModel.mensaje)
        coVerify(exactly = 0) { fuente.guardarObservacion(any(), any()) }
    }

    @Test
    fun observacionValidaSeGuardaSinEspaciosExternos() = runTest {
        val fuente = crearFuente()
        val viewModel = crearViewModel(fuente, this)

        viewModel.guardarObservacion(50, "  Necesita practicar lectura.  ")
        advanceUntilIdle()

        coVerify(exactly = 1) {
            fuente.guardarObservacion(50, "Necesita practicar lectura.")
        }
        assertEquals("Observación guardada correctamente.", viewModel.mensaje)
        assertFalse(viewModel.trabajando)
    }

    @Test
    fun errorDelRepositorioSeMuestraAlUsuario() = runTest {
        val fuente = crearFuente()
        coEvery { fuente.crearAsignacion(any()) } throws
                IllegalStateException("Error de prueba")
        val viewModel = crearViewModel(fuente, this)

        viewModel.elegirActividad(1)
        viewModel.elegirEstudiante("Estudiante A")
        viewModel.asignarActividad()
        advanceUntilIdle()

        assertTrue(viewModel.mensaje.contains("No se pudo guardar la asignación"))
        assertTrue(viewModel.mensaje.contains("Error de prueba"))
        assertFalse(viewModel.trabajando)
    }
}
