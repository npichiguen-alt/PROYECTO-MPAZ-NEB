
package com.example.mpazneb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mpazneb.ui.screens.administracion.NuevaActividadConRoomScreen
import com.example.mpazneb.ui.theme.MPAZNEBTheme

class AdministracionDemoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MPAZNEBTheme {
                NuevaActividadConRoomScreen()
            }
        }
    }
}
