package com.example.mpazneb.asistente

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.ResultadoAula
import com.example.mpazneb.ui.screens.asistente.SeguimientoAsistenteScreen
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de interfaz del módulo Asistente: seguimiento y observaciones.
 * Se ejecutan en un emulador o dispositivo, dentro de src/androidTest.
 */
@RunWith(AndroidJUnit4::class)
class SeguimientoAsistenteScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val completada = AsignacionAula(
        id = 101,
        actividadId = 1,
        actividadTitulo = "Cuento del bosque",
        estudianteAlias = "Estudiante A",
        codigoTemporal = "DEMO-204",
        asistenteAlias = "ASISTENTE_DEMO",
        curso = 2,
        estado = "Completada"
    )

    private val pendiente = AsignacionAula(
        id = 102,
        actividadId = 2,
        actividadTitulo = "Resolver sumas",
        estudianteAlias = "Estudiante B",
        codigoTemporal = "DEMO-305",
        asistenteAlias = "ASISTENTE_DEMO",
        curso = 2,
        estado = "Pendiente"
    )

    private val resultado = ResultadoAula(
        id = 50,
        asignacionId = 101,
        aciertos = 3,
        totalPreguntas = 4,
        intentos = 2,
        observacion = ""
    )

    private fun mostrarPantalla(
        asignaciones: List<AsignacionAula> = listOf(completada, pendiente),
        resultados: List<ResultadoAula> = listOf(resultado),
        trabajando: Boolean = false,
        alGuardar: (Int, String) -> Unit = { _, _ -> }
    ) {
        composeRule.setContent {
            MPAZNEBTheme {
                SeguimientoAsistenteScreen(
                    asignaciones = asignaciones,
                    resultados = resultados,
                    trabajando = trabajando,
                    onGuardarObservacion = alGuardar
                )
            }
        }
    }

    // Seguimiento usa LazyColumn. Sus primeros seis elementos son
    // encabezado, tres tarjetas de resumen, filtros y contador.
    // La primera asignación (o el mensaje de lista vacía) es el índice 6.
    private fun irAIndice(indice: Int) {
        composeRule.onNode(hasScrollToIndexAction())
            .performScrollToIndex(indice)
    }

    @Test
    fun sinAsignacionesMuestraEstadoVacio() {
        mostrarPantalla(asignaciones = emptyList(), resultados = emptyList())

        irAIndice(1)
        composeRule.onNodeWithText("0 asignaciones registradas.")
            .assertIsDisplayed()

        irAIndice(6)
        composeRule.onNodeWithText(
            "No hay asignaciones para el estado seleccionado."
        ).assertIsDisplayed()
    }

    @Test
    fun resumenMuestraContadoresCorrectos() {
        mostrarPantalla()

        irAIndice(1)
        composeRule.onNodeWithText("2 asignaciones registradas.")
            .assertIsDisplayed()

        irAIndice(2)
        composeRule.onNodeWithText("1 actividades completadas.")
            .assertIsDisplayed()

        irAIndice(3)
        composeRule.onNodeWithText("1 actividades pendientes.")
            .assertIsDisplayed()
    }

    @Test
    fun filtroPendienteMuestraSoloPendientes() {
        mostrarPantalla()

        irAIndice(4)
        composeRule.onNodeWithText("Pendiente")
            .performScrollTo()
            .performClick()

        irAIndice(5)
        composeRule.onNodeWithText("Asignaciones encontradas: 1")
            .assertIsDisplayed()

        irAIndice(6)
        composeRule.onNodeWithText("Resolver sumas")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Cuento del bosque")
            .assertDoesNotExist()
    }

    @Test
    fun filtroCompletadaMuestraSoloCompletadas() {
        mostrarPantalla()

        irAIndice(4)
        composeRule.onNodeWithText("Completada")
            .performScrollTo()
            .performClick()

        irAIndice(5)
        composeRule.onNodeWithText("Asignaciones encontradas: 1")
            .assertIsDisplayed()

        irAIndice(6)
        composeRule.onNodeWithText("Cuento del bosque")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Resolver sumas")
            .assertDoesNotExist()
    }

    @Test
    fun asignacionSinResultadoMuestraMensaje() {
        mostrarPantalla(
            asignaciones = listOf(pendiente),
            resultados = emptyList()
        )

        irAIndice(6)
        composeRule.onNodeWithText(
            "Esta actividad todavía no tiene resultados registrados."
        ).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun resultadoMuestraAciertosEIntentos() {
        mostrarPantalla(asignaciones = listOf(completada))

        irAIndice(6)
        composeRule.onNodeWithText("Aciertos: 3 de 4")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule.onNodeWithText("Intentos: 2")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun observacionVaciaNoSePuedeGuardar() {
        mostrarPantalla(asignaciones = listOf(completada))

        irAIndice(6)
        composeRule.onNodeWithText("Guardar observación")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    @Test
    fun observacionValidaSeEnviaSinEspaciosExternos() {
        var guardado: Pair<Int, String>? = null
        mostrarPantalla(
            asignaciones = listOf(completada),
            alGuardar = { id, texto -> guardado = id to texto }
        )

        irAIndice(6)
        composeRule.onNode(hasSetTextAction())
            .performScrollTo()
            .performTextReplacement("  Necesita practicar lectura.  ")

        composeRule.onNodeWithText("Guardar observación")
            .performScrollTo()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(50 to "Necesita practicar lectura.", guardado)
        }
    }

    @Test
    fun observacionNoAceptaMasDe250Caracteres() {
        var guardado: Pair<Int, String>? = null
        mostrarPantalla(
            asignaciones = listOf(completada),
            alGuardar = { id, texto -> guardado = id to texto }
        )

        irAIndice(6)
        composeRule.onNode(hasSetTextAction())
            .performScrollTo()
            .performTextReplacement("a".repeat(250))
        composeRule.onNodeWithText("250/250 caracteres")
            .performScrollTo()
            .assertIsDisplayed()

        // El campo debe rechazar un reemplazo de 251 caracteres.
        composeRule.onNode(hasSetTextAction())
            .performTextReplacement("b".repeat(251))
        composeRule.onNodeWithText("250/250 caracteres")
            .performScrollTo()
            .assertIsDisplayed()

        composeRule.onNodeWithText("Guardar observación")
            .performScrollTo()
            .performClick()

        composeRule.runOnIdle {
            assertEquals(50, guardado?.first)
            assertEquals("a".repeat(250), guardado?.second)
        }
    }

    @Test
    fun mientrasGuardaElBotonEstaDeshabilitado() {
        mostrarPantalla(
            asignaciones = listOf(completada),
            trabajando = true
        )

        irAIndice(6)
        composeRule.onNodeWithText("Guardando...")
            .performScrollTo()
            .assertIsNotEnabled()
    }
}
