
package com.example.mpazneb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember

import com.example.mpazneb.repository.asistente.DemoAsistenteDataSource
import com.example.mpazneb.ui.screens.asistente.AsistenteScreen
import com.example.mpazneb.ui.theme.MPAZNEBTheme

class AsistenteDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MPAZNEBTheme {

                // REPOSITORIO CON DATOS FICTICIOS
                val repositorio = remember {
                    DemoAsistenteDataSource()
                }

                // MOSTRAR EL MODULO COMPLETO
                AsistenteScreen(
                    fuente = repositorio
                )
            }
        }
    }
}
