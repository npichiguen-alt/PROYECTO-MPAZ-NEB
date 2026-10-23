
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.repository.administracion.ActividadRepository

@Composable
fun NuevaActividadConRoomScreen() {

    val contexto = LocalContext.current.applicationContext

    val repositorio = remember(contexto) {
        val database = ActividadDatabase.obtenerInstancia(contexto)
        ActividadRepository(database.actividadDao())
    }

    val fabrica = remember(repositorio) {
        viewModelFactory {
            initializer {
                AdministracionViewModel(repositorio)
            }
        }
    }

    val viewModel: AdministracionViewModel = viewModel(
        factory = fabrica
    )

    // Recordar qué pantalla está seleccionada
    var pantalla by rememberSaveable {
        mutableStateOf("Nueva")
    }

    // Consultar Room cuando abrimos Lectura o Matemática
    LaunchedEffect(pantalla) {
        if (
            pantalla == "Matemática" ||
            pantalla == "Lectura" ||
            pantalla == "Resultados"
        ){
            viewModel.cargarActividades()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // Botones para cambiar entre pantallas
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf("Nueva", "Matemática", "Lectura", "Ususarios", "Resultados").forEach { opcion ->

                FilterChip(
                    selected = pantalla == opcion,
                    onClick = { pantalla = opcion },
                    label = { Text(opcion) }
                )
            }
        }

        // Mostrar la pantalla elegida
        Box(
            modifier = Modifier.weight(1f)
        ) {
            when (pantalla) {

                "Nueva" -> {
                    NuevaActividadScreen(viewModel = viewModel)
                }

                "Matemática" -> {
                    GestionMatematicaScreen(
                        actividades = viewModel.actividades
                    )
                }

                "Lectura" -> {
                    GestionLecturaScreen(
                        actividades = viewModel.actividades
                    )
                }
                "Ususarios" -> {
                    AdministrarUsuariosScreen()
                }
                "Resultados" -> {
                    ResultadosGeneralesScreen(
                        actividades = viewModel.actividades
                    )
                }
            }
        }
    }
}
