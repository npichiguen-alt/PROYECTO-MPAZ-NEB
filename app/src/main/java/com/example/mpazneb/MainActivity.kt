
package com.example.mpazneb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.mpazneb.ui.screens.estudiante.IngresoEstudianteScreen
import com.example.mpazneb.ui.screens.SeleccionPerfilScreen
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.EstudianteViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val estudianteViewModel =
            ViewModelProvider(this)[EstudianteViewModel::class.java]

        setContent {

            MPAZNEBTheme {

                var mostrarIngresoEstudiante by rememberSaveable {
                    mutableStateOf(false)
                }

                val estadoEstudiante by
                estudianteViewModel.estado.collectAsState()

                BackHandler(enabled = mostrarIngresoEstudiante) {
                    mostrarIngresoEstudiante = false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->

                    if (mostrarIngresoEstudiante) {

                        IngresoEstudianteScreen(
                            estado = estadoEstudiante,
                            onCodigoChange = {
                                estudianteViewModel.actualizarCodigo(it)
                            },
                            onIngresar = {
                                estudianteViewModel.intentarIngresar()
                            },
                            modifier = Modifier.padding(innerPadding)
                        )

                    } else {

                        SeleccionPerfilScreen(
                            modifier = Modifier.padding(innerPadding),
                            onContinuarEstudiante = {
                                mostrarIngresoEstudiante = true
                            }
                        )
                    }
                }
            }
        }
    }
}
