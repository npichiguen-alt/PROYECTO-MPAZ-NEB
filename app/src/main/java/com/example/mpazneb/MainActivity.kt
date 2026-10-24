
package com.example.mpazneb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider

// Pantalla de selección de perfiles
import com.example.mpazneb.ui.screens.SeleccionPerfilScreen

// Módulo Estudiante de Noemí
import com.example.mpazneb.ui.screens.estudiante.IngresoEstudianteScreen
import com.example.mpazneb.viewmodel.EstudianteViewModel

// Módulo Asistente de Katy
import com.example.mpazneb.ui.screens.asistente.AsistenteScreen
import com.example.mpazneb.repository.asistente.DemoAsistenteDataSource

// Tema de la aplicación
import com.example.mpazneb.ui.theme.MPAZNEBTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ViewModel del módulo Estudiante
        val estudianteViewModel =
            ViewModelProvider(this)[EstudianteViewModel::class.java]

        setContent {

            MPAZNEBTheme {

                // Controla qué pantalla está abierta
                var pantallaActual by rememberSaveable {
                    mutableStateOf("PERFILES")
                }

                // Estado del ingreso del estudiante
                val estadoEstudiante by
                estudianteViewModel.estado.collectAsState()

                // Fuente de datos de prueba del Asistente
                val fuenteAsistente = remember {
                    DemoAsistenteDataSource()
                }

                // Volver a la selección de perfiles
                BackHandler(
                    enabled = pantallaActual != "PERFILES"
                ) {
                    pantallaActual = "PERFILES"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    when (pantallaActual) {

                        // ==========================
                        // PERFIL ESTUDIANTE
                        // ==========================

                        "ESTUDIANTE" -> {

                            IngresoEstudianteScreen(
                                estado = estadoEstudiante,

                                onCodigoChange = { codigo ->
                                    estudianteViewModel
                                        .actualizarCodigo(codigo)
                                },

                                onIngresar = {
                                    estudianteViewModel
                                        .intentarIngresar()
                                },

                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        // ==========================
                        // PERFIL ASISTENTE DE AULA
                        // ==========================

                        "ASISTENTE" -> {

                            Box(
                                modifier = Modifier.padding(innerPadding)
                            ) {

                                AsistenteScreen(
                                    fuente = fuenteAsistente
                                )
                            }
                        }

                        // ==========================
                        // SELECCIÓN DE PERFILES
                        // ==========================

                        else -> {

                            SeleccionPerfilScreen(
                                modifier = Modifier.padding(innerPadding),

                                onContinuarEstudiante = {
                                    pantallaActual = "ESTUDIANTE"
                                },

                                onContinuarAsistente = {
                                    pantallaActual = "ASISTENTE"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
