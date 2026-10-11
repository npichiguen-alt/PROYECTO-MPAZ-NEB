package com.example.mpazneb.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.EstudianteViewModel

@Composable
fun NavegacionEstudianteDemo() {

    // Guarda la pantalla que estamos mostrando
    var pantallaActual by rememberSaveable {
        mutableStateOf("inicio")
    }

    // ViewModel utilizado solamente para esta demostración
    val estudianteViewModel = remember {
        EstudianteViewModel()
    }

    val resultados by estudianteViewModel.resultados.collectAsState()


    // Permite regresar usando Atrás del celular
    BackHandler(enabled = pantallaActual != "inicio") {
        pantallaActual = "inicio"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Botón para regresar al inicio
        if (pantallaActual != "inicio") {
            TextButton(
                onClick = {
                    pantallaActual = "inicio"
                }
            ) {
                Text("← Volver al inicio")
            }
        }

        // Mostramos la pantalla seleccionada
        when (pantallaActual) {

            "inicio" -> {
                InicioEstudianteScreen(
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
