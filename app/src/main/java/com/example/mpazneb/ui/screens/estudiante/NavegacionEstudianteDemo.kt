package com.example.mpazneb.ui.screens.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.EstudianteViewModel

@Composable
fun NavegacionEstudianteDemo() {

    // ====================================
    // 1. PANTALLA ACTUAL
    // ====================================

    var pantallaActual by rememberSaveable {
        mutableStateOf("inicio")
    }

    // Utilizar el ViewModel del Estudiante
    val estudianteViewModel: EstudianteViewModel = viewModel()

    // ====================================
    // 2. INFORMACIÓN DEL ESTUDIANTE
    // ====================================

    val estadoIngreso by
    estudianteViewModel.estado.collectAsState()

    val asignacionActiva =
        estadoIngreso.asignacionActiva

    // ====================================
    // 3. ACTIVIDADES ASIGNADAS
    // ====================================

    // IDs utilizados en la demostración
    // ID 1 = Lectura
    // ID 2 = Matemática

    val mostrarLectura =
        asignacionActiva?.actividadId == 1

    val mostrarMatematica =
        asignacionActiva?.actividadId == 2

    // ====================================
    // 4. ESTADOS DE LAS ACTIVIDADES
    // ====================================

    val estadoLectura by
    estudianteViewModel.estadoLectura.collectAsState()

    val estadoMatematica by
    estudianteViewModel.estadoMatematica.collectAsState()

    // ====================================
    // 5. RESULTADOS
    // ====================================

    val resultados by
    estudianteViewModel.resultados.collectAsState()

    // ====================================
    // 6. HISTORIAL DE RESPUESTAS
    // ====================================

    val respuestas by
    estudianteViewModel.respuestas.collectAsState()

    // ====================================
    // 7. BOTÓN ATRÁS
    // ====================================

    BackHandler(
        enabled = pantallaActual != "inicio"
    ) {
        pantallaActual = "inicio"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (pantallaActual != "inicio") {

            TextButton(
                onClick = {
                    pantallaActual = "inicio"
                }
            ) {
                Text("← Volver al inicio")
            }
        }

        // ====================================
        // 8. NAVEGACIÓN ENTRE PANTALLAS
        // ====================================

        when (pantallaActual) {

            // ====================================
            // PANTALLA DE INICIO
            // ====================================

            "inicio" -> {

                InicioEstudianteScreen(

                    // La actividad terminó:
                    // por acierto o por intentos agotados
                    lecturaCompletada =
                        estadoLectura.finalizada,

                    matematicaCompletada =
                        estadoMatematica.finalizada,

                    // La actividad terminó
                    // con una respuesta correcta
                    lecturaAprobada =
                        estadoLectura.finalizada &&
                                estadoLectura.esCorrecta == true,

                    matematicaAprobada =
                        estadoMatematica.finalizada &&
                                estadoMatematica.esCorrecta == true,

                    // Mostrar solo la actividad asignada
                    mostrarLectura = mostrarLectura,
                    mostrarMatematica = mostrarMatematica,

                    onAbrirLectura = {
                        if (mostrarLectura) {
                            pantallaActual = "lectura"
                        }
                    },

                    onAbrirMatematica = {
                        if (mostrarMatematica) {
                            pantallaActual = "matematica"
                        }
                    },

                    onAbrirResultados = {
                        pantallaActual = "resultados"
                    }
                )
            }

            // ====================================
            // ACTIVIDAD DE LECTURA
            // ====================================

            "lectura" -> {

                if (mostrarLectura) {

                    ActividadLecturaConViewModel(
                        estudianteViewModel =
                            estudianteViewModel
                    )

                } else {

                    Text(
                        "Esta actividad no está asignada."
                    )

                    TextButton(
                        onClick = {
                            pantallaActual = "inicio"
                        }
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }

            // ====================================
            // ACTIVIDAD DE MATEMÁTICA
            // ====================================

            "matematica" -> {

                if (mostrarMatematica) {

                    ActividadMatematicaConViewModel(
                        estudianteViewModel =
                            estudianteViewModel
                    )

                } else {

                    Text(
                        "Esta actividad no está asignada."
                    )

                    TextButton(
                        onClick = {
                            pantallaActual = "inicio"
                        }
                    ) {
                        Text("Volver al inicio")
                    }
                }
            }

            // ====================================
            // MIS RESULTADOS
            // ====================================

            "resultados" -> {

                ResultadosEstudianteScreen(
                    resultados = resultados,
                    respuestas = respuestas
                )
            }

            // ====================================
            // PANTALLA DESCONOCIDA
            // ====================================

            else -> {

                TextButton(
                    onClick = {
                        pantallaActual = "inicio"
                    }
                ) {
                    Text("Ir al inicio")
                }
            }
        }
    }
}

// ====================================
// 9. VISTA PREVIA
// ====================================

@Preview(showBackground = true)
@Composable
fun NavegacionEstudianteDemoPreview() {

    MPAZNEBTheme {
        NavegacionEstudianteDemo()
    }
}