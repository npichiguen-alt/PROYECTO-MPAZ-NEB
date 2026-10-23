
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
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

    NuevaActividadScreen(viewModel = viewModel)
}
