
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.example.mpazneb.viewmodel.EstudianteViewModel
import androidx.compose.runtime.remember

@Composable
fun ActividadLecturaScreen(
    respuestaSeleccionada: String? = null,
    onSeleccionarRespuesta: (String) -> Unit = {},
    onComprobar: () -> Unit = {},
    mensaje: String = "",
    esCorrecta: Boolean? = null,
    finalizada: Boolean = false,
    modifier: Modifier = Modifier
) {

    val alternativas = listOf(
        "Un gato",
        "Un perro",
        "Un pájaro"
    )

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
            text = "Actividad de Lectura",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    text = "El gato Tomás",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tomás es un gato pequeño que vive " +
                            "en una casa con jardín. Todas las mañanas " +
                            "sale a jugar con una pelota roja. " +
                            "Después vuelve a casa para descansar."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¿Qué animal es Tomás?",
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
                    },
                    enabled = !finalizada
                )

                Text(text = alternativa)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onComprobar,
            enabled = respuestaSeleccionada != null && !finalizada,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comprobar respuesta")
        }

        if (finalizada) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Actividad completada!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
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

@Preview(showBackground = true)
@Composable
fun ActividadLecturaPreview() {
    MPAZNEBTheme {

        val estudianteViewModel = remember {
            EstudianteViewModel()
        }

        ActividadLecturaConViewModel(
            estudianteViewModel = estudianteViewModel
        )
    }
}
@Composable
fun ActividadLecturaConViewModel(
    estudianteViewModel: EstudianteViewModel,
    modifier: Modifier = Modifier
) {
    val estadoLectura by
    estudianteViewModel.estadoLectura.collectAsState()

    ActividadLecturaScreen(
        respuestaSeleccionada =
            estadoLectura.respuestaSeleccionada,

        onSeleccionarRespuesta = { respuesta ->
            estudianteViewModel
                .seleccionarRespuestaLectura(respuesta)
        },

        finalizada = estadoLectura.finalizada,

        onComprobar = {
            estudianteViewModel.comprobarRespuestaLectura()
        },

        mensaje = estadoLectura.mensaje,
        esCorrecta = estadoLectura.esCorrecta,
        modifier = modifier



    )
}

