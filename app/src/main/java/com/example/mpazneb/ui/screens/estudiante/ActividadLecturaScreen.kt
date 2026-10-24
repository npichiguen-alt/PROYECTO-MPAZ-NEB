
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.EstudianteViewModel

// ====================================
// 1. PANTALLA DE LECTURA
// ====================================

@Composable
fun ActividadLecturaScreen(
    respuestaSeleccionada: String? = null,
    onSeleccionarRespuesta: (String) -> Unit = {},
    onComprobar: () -> Unit = {},
    mensaje: String = "",
    esCorrecta: Boolean? = null,
    finalizada: Boolean = false,
    intentos: Int = 0,
    maxIntentos: Int = 3,
    modifier: Modifier = Modifier
) {

    // Alternativas de demostración
    val alternativas = listOf(
        "Un gato",
        "Un perro",
        "Un pájaro"
    )

    // ====================================
    // 2. CONTROL DE INTENTOS
    // ====================================

    // El estudiante agotó los intentos
    // si llegó al máximo y no respondió correctamente
    val intentosAgotados =
        intentos >= maxIntentos && !finalizada

    // Bloquear controles cuando termina la actividad
    // o se agotan los intentos
    val controlesBloqueados =
        finalizada || intentosAgotados

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ====================================
        // 3. ENCABEZADO
        // ====================================

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

        // ====================================
        // 4. TEXTO DE LECTURA
        // ====================================

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

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

        // ====================================
        // 5. PREGUNTA
        // ====================================

        Text(
            text = "¿Qué animal es Tomás?",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====================================
        // 6. ALTERNATIVAS
        // ====================================

        alternativas.forEach { alternativa ->

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected =
                        respuestaSeleccionada == alternativa,

                    onClick = {
                        onSeleccionarRespuesta(alternativa)
                    },

                    // NUEVO:
                    // No permitir seleccionar alternativas
                    // después de completar o agotar intentos
                    enabled = !controlesBloqueados
                )

                Text(
                    text = alternativa
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ====================================
        // 7. CONTADOR DE INTENTOS
        // ====================================

        Text(
            text = "Intentos realizados: $intentos de $maxIntentos",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====================================
        // 8. BOTÓN COMPROBAR
        // ====================================

        Button(
            onClick = onComprobar,

            // NUEVO:
            // Bloquear si terminó o agotó intentos
            enabled =
                respuestaSeleccionada != null &&
                        !controlesBloqueados,

            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Comprobar respuesta")
        }

        // ====================================
        // 9. ACTIVIDAD COMPLETADA
        // ====================================

        AnimatedVisibility(
            visible = finalizada,
            enter = fadeIn() + expandVertically()
        ) {

            Column {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¡Actividad completada!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // ====================================
        // 10. MENSAJE DE INTENTOS AGOTADOS
        // ====================================

        if (intentosAgotados) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "No quedan intentos disponibles.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ====================================
        // 11. MENSAJE DEL RESULTADO
        // ====================================

        if (mensaje.isNotEmpty()) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,

                color = when (esCorrecta) {

                    true ->
                        MaterialTheme.colorScheme.primary

                    false ->
                        MaterialTheme.colorScheme.error

                    null ->
                        MaterialTheme.colorScheme.onSurfaceVariant
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

// ====================================
// 12. CONEXIÓN CON EL VIEWMODEL
// ====================================

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

        onComprobar = {

            estudianteViewModel
                .comprobarRespuestaLectura()
        },

        mensaje = estadoLectura.mensaje,

        esCorrecta = estadoLectura.esCorrecta,

        finalizada = estadoLectura.finalizada,

        // NUEVO: enviar el contador del ViewModel
        intentos = estadoLectura.intentos,

        maxIntentos = 3,

        modifier = modifier
    )
}

// ====================================
// 13. VISTA PREVIA
// ====================================

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
