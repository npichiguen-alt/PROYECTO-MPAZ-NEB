
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
import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.repository.asistente.RoomAsistenteDataSource


// Módulo Administración
import com.example.mpazneb.ui.screens.administracion.NuevaActividadConRoomScreen

// Tema de la aplicación
import com.example.mpazneb.ui.theme.MPAZNEBTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // ====================================
        // VIEWMODEL DEL ESTUDIANTE CON ROOM
        // ====================================

        val estudianteViewModel = ViewModelProvider(
            this,
            EstudianteViewModelFactory(applicationContext)
        )[EstudianteViewModel::class.java]

        setContent {

            MPAZNEBTheme {

                // ====================================
                // PANTALLA ACTUAL
                // ====================================

                var pantallaActual by rememberSaveable {
                    mutableStateOf("PERFILES")
                }

                // Observar el ingreso del estudiante
                val estadoEstudiante by
                estudianteViewModel.estado.collectAsState()


                // Actividades reales de Administración mediante Room
                val fuenteAsistente = remember {
                    RoomAsistenteDataSource(
                        actividadDao = ActividadDatabase
                            .obtenerInstancia(applicationContext)
                            .actividadDao()
                    )
                }


                // ====================================
                // VOLVER A SELECCIÓN DE PERFILES
                // ====================================

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

                                // Código válido:
                                // mostrar la actividad asignada.
                                Box(
                                    modifier = Modifier.padding(
                                        innerPadding
                                    )
                                ) {

                                    // IMPORTANTE:
                                    // Usar el mismo ViewModel
                                    // que validó el código.
                                    NavegacionEstudianteDemo(
                                        estudianteViewModel =
                                            estudianteViewModel
                                    )
                                }

                            } else {

                                // Código todavía no validado:
                                // mostrar pantalla de ingreso.
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
                        // PERFIL ASISTENTE DE AULA
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
