package com.example.mpazneb.asistente

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.FormularioAsignacion
import com.example.mpazneb.ui.screens.asistente.AsignarActividadScreen
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AsignarActividadScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val actividadesPrueba = listOf(
        ActividadAula(
            id = 1,
            titulo = "Cuento del bosque",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Lectura de prueba",
            estado = "Disponible"
        ),
        ActividadAula(
            id = 5,
            titulo = "Borrador de lectura",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Todavía no disponible",
            estado = "Borrador"
        )
    )

    private val estudiantesPrueba = listOf(
        EstudianteAula(alias = "Estudiante A", activo = true),
        EstudianteAula(alias = "Estudiante D", activo = false)
    )

    /** La pantalla se maneja con estado local para probar la UI sin Room. */
    private fun mostrarPantalla(
        formularioInicial: FormularioAsignacion = FormularioAsignacion(),
        actividades: List<ActividadAula> = actividadesPrueba,
        estudiantes: List<EstudianteAula> = estudiantesPrueba,
        trabajando: Boolean = false,
        onAsignar: () -> Unit = {}
    ) {
        composeRule.setContent {
            var formulario by remember { mutableStateOf(formularioInicial) }

            MPAZNEBTheme {
                AsignarActividadScreen(
                    formulario = formulario,
                    actividades = actividades,
                    estudiantes = estudiantes,
                    trabajando = trabajando,
                    onModuloChange = {
                        formulario = formulario.copy(modulo = it, actividadId = 0)
                    },
                    onCursoChange = {
                        formulario = formulario.copy(curso = it, actividadId = 0)
                    },
                    onDificultadChange = {
                        formulario = formulario.copy(dificultad = it, actividadId = 0)
                    },
                    onActividadChange = {
                        formulario = formulario.copy(actividadId = it)
                    },
                    onEstudianteChange = {
                        formulario = formulario.copy(estudianteAlias = it)
                    },
                    onAsignar = onAsignar
                )
            }
        }
    }

    private fun seleccionarActividadYEstudiante() {
        composeRule.onNodeWithText("Cuento del bosque")
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("Estudiante A")
            .performScrollTo()
            .performClick()
    }

    private fun revisarAsignacion() {
        composeRule.onNodeWithText("Revisar y asignar")
            .performScrollTo()
            .performClick()
    }

    @Test
    fun pantallaMuestraActividadDisponible() {
        mostrarPantalla()
        composeRule.onNodeWithText("Asignar actividad").assertIsDisplayed()
        composeRule.onNodeWithText("Cuento del bosque").assertExists()
        composeRule.onNodeWithText("Borrador de lectura").assertDoesNotExist()
    }

    @Test
    fun estudianteInactivoNoApareceEnFormulario() {
        mostrarPantalla()
        composeRule.onNodeWithText("Estudiante A").assertExists()
        composeRule.onNodeWithText("Estudiante D").assertDoesNotExist()
    }

    @Test
    fun formularioSinActividadMuestraError() {
        mostrarPantalla()
        revisarAsignacion()
        composeRule.onNodeWithText("Selecciona una actividad.")
            .assertIsDisplayed()
    }

    @Test
    fun formularioSinEstudianteMuestraError() {
        mostrarPantalla()
        composeRule.onNodeWithText("Cuento del bosque")
            .performScrollTo()
            .performClick()
        revisarAsignacion()
        composeRule.onNodeWithText("Selecciona un estudiante.")
            .assertIsDisplayed()
    }

    @Test
    fun asignacionValidaAbreConfirmacionYSolicitaGuardar() {
        var vecesAsignada = 0
        mostrarPantalla(onAsignar = { vecesAsignada++ })
        seleccionarActividadYEstudiante()
        revisarAsignacion()
        composeRule.onNodeWithText("Confirmar asignación").assertIsDisplayed()
        composeRule.onNodeWithText("Confirmar").performClick()
        composeRule.runOnIdle { assertEquals(1, vecesAsignada) }
    }

    @Test
    fun cancelarConfirmacionNoGuardaAsignacion() {
        var vecesAsignada = 0
        mostrarPantalla(onAsignar = { vecesAsignada++ })
        seleccionarActividadYEstudiante()
        revisarAsignacion()
        composeRule.onNodeWithText("Cancelar").performClick()
        composeRule.onNodeWithText("Confirmar asignación").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(0, vecesAsignada) }
    }

    @Test
    fun botonSeDeshabilitaMientrasGuarda() {
        mostrarPantalla(trabajando = true)
        composeRule.onNodeWithText("Guardando...")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun pantallaInformaCuandoNoHayActividadesDisponibles() {
        mostrarPantalla(actividades = actividadesPrueba.filter { it.estado == "Borrador" })
        composeRule.onNodeWithText(
            "No hay actividades disponibles con estos filtros. Prueba otro curso o dificultad."
        ).assertIsDisplayed()
    }
}
