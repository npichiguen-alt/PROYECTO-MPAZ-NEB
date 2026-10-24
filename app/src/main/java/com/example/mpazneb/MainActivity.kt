
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

// Selección de perfiles
import com.example.mpazneb.ui.screens.SeleccionPerfilScreen

// Módulo Estudiante
import com.example.mpazneb.ui.screens.estudiante.IngresoEstudianteScreen
import com.example.mpazneb.ui.screens.estudiante.NavegacionEstudianteDemo
import com.example.mpazneb.viewmodel.EstudianteViewModel
import com.example.mpazneb.viewmodel.EstudianteViewModelFactory

// Módulo Asistente
import com.example.mpazneb.ui.screens.asistente.AsistenteScreen
import com.example.mpazneb.repository.asistente.DemoAsistenteDataSource

// Módulo Administración
import com.example.mpazneb.ui.screens.administracion.NuevaActividadConRoomScreen

// Tema de la aplicación
import com.example.mpazneb.ui.theme.MPAZNEBTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val estudianteViewModel = ViewModelProvider(
            this,
            EstudianteViewModelFactory(applicationContext)
        )[EstudianteViewModel::class.java]

        setContent {

            MPAZNEBTheme {

                // Guarda qué perfil está abierto
                var pantallaActual by rememberSaveable {
                    mutableStateOf("PERFILES")
                }

                // Observa el ingreso del estudiante
                val estadoEstudiante by
                estudianteViewModel.estado.collectAsState()

                // Datos ficticios del Asistente
                val fuenteAsistente = remember {
                    DemoAsistenteDataSource()
                }

                // Volver a seleccionar perfil
                BackHandler(
                    enabled = pantallaActual != "PERFILES"
                ) {
                    pantallaActual = "PERFILES"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    when (pantallaActual) {

                        // ====================================
                        // PERFIL ESTUDIANTE
                        // ====================================

                        "ESTUDIANTE" -> {

                            if (estadoEstudiante.ingresoValidado) {

                                // Código correcto:
                                // mostrar el módulo Estudiante
                                Box(
                                    modifier = Modifier.padding(
                                        innerPadding
                                    )
                                ) {
                                    NavegacionEstudianteDemo()
                                }

                            } else {

                                // Código no validado:
                                // mostrar pantalla de ingreso
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

                                    modifier = Modifier.padding(
                                        innerPadding
                                    )
                                )
                            }
                        }

                        // ====================================
                        // PERFIL ASISTENTE
                        // ====================================

                        "ASISTENTE" -> {

                            Box(
                                modifier = Modifier.padding(
                                    innerPadding
                                )
                            ) {
                                AsistenteScreen(
                                    fuente = fuenteAsistente
                                )
                            }
                        }

                        // ====================================
                        // PERFIL ADMINISTRACIÓN
                        // ====================================

                        "ADMINISTRACION" -> {

                            Box(
                                modifier = Modifier.padding(
                                    innerPadding
                                )
                            ) {
                                NuevaActividadConRoomScreen()
                            }
                        }

                        // ====================================
                        // SELECCIÓN DE PERFILES
                        // ====================================

                        else -> {

                            SeleccionPerfilScreen(
                                modifier = Modifier.padding(
                                    innerPadding
                                ),

                                onContinuarEstudiante = {

                                    // Limpiar la sesión anterior
                                    estudianteViewModel
                                        .actualizarCodigo("")

                                    pantallaActual = "ESTUDIANTE"
                                },

                                onContinuarAsistente = {
                                    pantallaActual = "ASISTENTE"
                                },

                                onContinuarAdministracion = {
                                    pantallaActual = "ADMINISTRACION"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
