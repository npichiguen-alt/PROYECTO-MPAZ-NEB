package com.example.mpazneb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpazneb.model.PerfilUsuario
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun SeleccionPerfilScreen(modifier: Modifier = Modifier) {

    var perfilSeleccionado by rememberSaveable {
        mutableStateOf("")
    }

    var mensaje by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Nivelación Lectura y Matemática"
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Selecciona tu perfil",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        PerfilUsuario.entries.forEach { perfil ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = perfilSeleccionado == perfil.nombreVisible,
                    onClick = {
                        perfilSeleccionado = perfil.nombreVisible
                        mensaje = ""
                    }
                )

                Text(text = perfil.nombreVisible)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                mensaje = "Seleccionaste: $perfilSeleccionado"
            },
            enabled = perfilSeleccionado.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = mensaje)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Datos ficticios para uso académico",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SeleccionPerfilPreview() {
    MPAZNEBTheme {
        SeleccionPerfilScreen()
    }
}
