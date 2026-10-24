
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
// 1. PANTALLA DE MATEMÁTICA
// ====================================

@Composable
fun ActividadMatematicaScreen(
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
        "10",
        "12",
        "14"
    )

    // ====================================
    // 2. CONTROL DE INTENTOS
    // ====================================

    // Detectar si el estudiante agotó los intentos
    // sin completar correctamente la actividad
    val intentosAgotados =
        intentos >= maxIntentos && !finalizada

    // Bloquear las alternativas y el botón
    // cuando termine o agote sus intentos
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
            text = "Actividad de Matemática",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ====================================
        // 4. EJERCICIO MATEMÁTICO
        // ====================================

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

        // ====================================
        // 5. PREGUNTA
        // ====================================

        Text(
            text = "¿Cuál es el resultado correcto?",
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

                    // No permitir nuevas selecciones
                    // si completó o agotó los intentos
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

            // Se deshabilita si:
            // - No hay respuesta seleccionada
            // - La actividad ya terminó
            // - Se agotaron los intentos
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
        // 10. INTENTOS AGOTADOS
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

            estudianteViewModel
                .comprobarRespuestaMatematica()
        },

        mensaje = estadoMatematica.mensaje,

        esCorrecta = estadoMatematica.esCorrecta,

        finalizada = estadoMatematica.finalizada,

        // Recuperar el número de intentos
        // desde el ViewModel
        intentos = estadoMatematica.intentos,

        // Máximo provisional de la demostración
        maxIntentos = 3,

        modifier = modifier
    )
}

// ====================================
// 13. VISTA PREVIA
// ====================================

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
