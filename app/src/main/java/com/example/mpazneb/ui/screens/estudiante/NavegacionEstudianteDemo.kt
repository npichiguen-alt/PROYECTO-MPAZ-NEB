
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

    // Pantalla que se está mostrando
    var pantallaActual by rememberSaveable {
        mutableStateOf("inicio")
    }

    // Usar el mismo ViewModel de MainActivity
    val estudianteViewModel: EstudianteViewModel = viewModel()

    // Estados de Lectura, Matemática y Resultados
    val estadoLectura by
    estudianteViewModel.estadoLectura.collectAsState()

    val estadoMatematica by
    estudianteViewModel.estadoMatematica.collectAsState()

    val resultados by
    estudianteViewModel.resultados.collectAsState()

    // Regresar con el botón Atrás del teléfono
    BackHandler(
        enabled = pantallaActual != "inicio"
    ) {
        pantallaActual = "inicio"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Botón para volver al inicio
        if (pantallaActual != "inicio") {

            TextButton(
                onClick = {
                    pantallaActual = "inicio"
                }
            ) {
                Text("← Volver al inicio")
            }
        }

        // Navegación entre las pantallas
        when (pantallaActual) {

            "inicio" -> {

                InicioEstudianteScreen(
                    lecturaCompletada =
                        estadoLectura.finalizada,

                    matematicaCompletada =
                        estadoMatematica.finalizada,

                    onAbrirLectura = {
                        pantallaActual = "lectura"
                    },

                    onAbrirMatematica = {
                        pantallaActual = "matematica"
                    },

                    onAbrirResultados = {
                        pantallaActual = "resultados"
                    }
                )
            }

            "lectura" -> {

                ActividadLecturaConViewModel(
                    estudianteViewModel = estudianteViewModel
                )
            }

            "matematica" -> {

                ActividadMatematicaConViewModel(
                    estudianteViewModel = estudianteViewModel
                )
            }

            "resultados" -> {

                ResultadosEstudianteScreen(
                    resultados = resultados
                )
            }

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

@Preview(showBackground = true)
@Composable
fun NavegacionEstudianteDemoPreview() {

    MPAZNEBTheme {
        NavegacionEstudianteDemo()
    }
}

