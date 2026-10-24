
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.EstudianteViewModel

// COLORES DEL MÓDULO ESTUDIANTE
private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulSuave = Color(0xFFE7F5FE)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White
private val RojoError = Color(0xFFB3264D)

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

    // Alternativas originales
    val alternativas = listOf(
        "10",
        "12",
        "14"
    )

    // Mantener el control original de intentos
    val intentosAgotados =
        intentos >= maxIntentos && !finalizada

    val controlesBloqueados =
        finalizada || intentosAgotados

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CelestePastel)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ====================================
        // 2. ENCABEZADO
        // ====================================

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "MPAZ NEB",
                style = MaterialTheme.typography.titleMedium,
                color = Rosa
            )

            Text(
                text = "Actividad de Matemática",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulOscuro
            )

            Text(
                text = "Resuelve la operación y selecciona la respuesta correcta.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )
        }

        // ====================================
        // 3. EJERCICIO MATEMÁTICO
        // ====================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                Text(
                    text = "Resuelve la operación",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulOscuro
                )

                Surface(
                    color = AzulSuave,
                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text(
                        text = "7 + 5 = ?",
                        modifier = Modifier.padding(
                            horizontal = 30.dp,
                            vertical = 22.dp
                        ),
                        style = MaterialTheme.typography.headlineLarge,
                        color = AzulOscuro,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // ====================================
        // 4. PREGUNTA Y ALTERNATIVAS
        // ====================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Selecciona una alternativa",
                    style = MaterialTheme.typography.labelLarge,
                    color = Azul
                )

                Text(
                    text = "¿Cuál es el resultado correcto?",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro
                )

                alternativas.forEach { alternativa ->

                    val seleccionada =
                        respuestaSeleccionada == alternativa

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = if (seleccionada) {
                            RosaSuave
                        } else {
                            AzulSuave
                        }
                    ) {

                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            RadioButton(
                                selected = seleccionada,
                                onClick = {
                                    onSeleccionarRespuesta(
                                        alternativa
                                    )
                                },
                                enabled = !controlesBloqueados,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = Rosa,
                                    unselectedColor = Azul
                                )
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = alternativa,
                                style = MaterialTheme.typography.bodyLarge,
                                color = AzulOscuro
                            )
                        }
                    }
                }
            }
        }

        // ====================================
        // 5. CONTADOR DE INTENTOS
        // ====================================

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Intentos realizados: $intentos de $maxIntentos",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulOscuro
                )

                LinearProgressIndicator(
                    progress = {
                        if (maxIntentos > 0) {
                            (
                                    intentos.toFloat() / maxIntentos
                                    ).coerceIn(0f, 1f)
                        } else {
                            0f
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp),
                    color = Rosa,
                    trackColor = RosaSuave
                )

                // BOTÓN COMPROBAR
                Button(
                    onClick = onComprobar,
                    enabled =
                        respuestaSeleccionada != null &&
                                !controlesBloqueados,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Rosa,
                        contentColor = Blanco,
                        disabledContainerColor = RosaSuave,
                        disabledContentColor = GrisTexto
                    )
                ) {

                    Text(
                        text = "Comprobar respuesta",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // ====================================
        // 6. ACTIVIDAD COMPLETADA
        // ====================================

        AnimatedVisibility(
            visible = finalizada,
            enter = fadeIn() + expandVertically()
        ) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RosaSuave,
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "¡Actividad completada!",
                    modifier = Modifier.padding(18.dp),
                    color = AzulOscuro,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ====================================
        // 7. INTENTOS AGOTADOS
        // ====================================

        if (intentosAgotados) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RosaSuave,
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "No quedan intentos disponibles.",
                    modifier = Modifier.padding(18.dp),
                    color = RojoError,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // ====================================
        // 8. MENSAJE DE RESPUESTA
        // ====================================

        if (mensaje.isNotEmpty()) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blanco,
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = mensaje,
                    modifier = Modifier.padding(18.dp),
                    color = when (esCorrecta) {
                        true -> Azul
                        false -> RojoError
                        null -> GrisTexto
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        // ====================================
        // 9. PIE DE PÁGINA
        // ====================================

        Text(
            text = "Actividad ficticia de demostración",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )
    }
}

// ====================================
// 10. CONEXIÓN CON EL VIEWMODEL
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

        intentos = estadoMatematica.intentos,

        maxIntentos = 3,

        modifier = modifier
    )
}

// ====================================
// 11. VISTA PREVIA
// ====================================

@Preview(showBackground = true)
@Composable
fun ActividadMatematicaPreview() {

    MPAZNEBTheme {
        ActividadMatematicaScreen(
            respuestaSeleccionada = "12",
            intentos = 1
        )
    }
}
