
package com.example.mpazneb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.mpazneb.viewmodel.EstudianteViewModel

@Composable
fun ActividadMatematicaScreen(
    respuestaSeleccionada: String? = null,
    onSeleccionarRespuesta: (String) -> Unit = {},
    onComprobar: () -> Unit = {},
    mensaje: String = "",
    esCorrecta: Boolean? = null,
    modifier: Modifier = Modifier
) {

    val alternativas = listOf("10", "12", "14")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Actividad de Matemática",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Resuelve la operación",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "7 + 5 = ?",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Cuál es el resultado correcto?",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        alternativas.forEach { alternativa ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = respuestaSeleccionada == alternativa,
                    onClick = {
                        onSeleccionarRespuesta(alternativa)
                    }
                )

                Text(text = alternativa)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onComprobar,
            enabled = respuestaSeleccionada != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comprobar respuesta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                color = when (esCorrecta) {
                    true -> MaterialTheme.colorScheme.primary
                    false -> MaterialTheme.colorScheme.error
                    null -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actividad ficticia de demostración",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ActividadMatematicaConViewModel(
    estudianteViewModel: EstudianteViewModel,
    modifier: Modifier = Modifier
) {
    val estadoMatematica by
    estudianteViewModel.estadoMatematica.collectAsState()

    ActividadMatematicaScreen(
        respuestaSeleccionada =
            estadoMatematica.respuestaSeleccionada,

        onSeleccionarRespuesta = { respuesta ->
            estudianteViewModel
                .seleccionarRespuestaMatematica(respuesta)
        },

        onComprobar = {
            estudianteViewModel.comprobarRespuestaMatematica()
        },

        mensaje = estadoMatematica.mensaje,
        esCorrecta = estadoMatematica.esCorrecta,
        modifier = modifier
    )
}


@Preview(showBackground = true)
@Composable
fun ActividadMatematicaPreview() {
    MPAZNEBTheme {

        val estudianteViewModel = remember {
            EstudianteViewModel()
        }

        ActividadMatematicaConViewModel(
            estudianteViewModel = estudianteViewModel
        )
    }
}

