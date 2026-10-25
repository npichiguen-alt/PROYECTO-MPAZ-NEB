package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// Colores del módulo Estudiante
private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulSuave = Color(0xFFE7F5FE)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White
private val RojoError = Color(0xFFB3264D)

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

    // Actividad ficticia de demostración
    val alternativas = listOf(
        "Un gato",
        "Un perro",
        "Un pájaro"
    )

    // Diferenciar éxito de intentos agotados
    val intentosAgotados =
        intentos >= maxIntentos && esCorrecta != true

    val actividadAprobada =
        finalizada && esCorrecta == true

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

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "MPAZ NEB",
                style = MaterialTheme.typography.titleMedium,
                color = Rosa
            )

            Text(
                text = "Actividad de Lectura",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulOscuro
            )

            Text(
                text = "Lee con atención y responde la pregunta.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )
        }

        // CUENTO
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Mi lectura",
                    style = MaterialTheme.typography.labelLarge,
                    color = Rosa
                )

                Text(
                    text = "El gato Tomás",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro
                )

                HorizontalDivider(color = AzulSuave)

                Text(
                    text = "Tomás es un gato pequeño que vive " +
                            "en una casa con jardín. Todas las mañanas " +
                            "sale a jugar con una pelota roja. " +
                            "Después vuelve a casa para descansar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AzulOscuro
                )
            }
        }

        // PREGUNTA
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
                    text = "Pregunta de comprensión",
                    style = MaterialTheme.typography.labelLarge,
                    color = Azul
                )

                Text(
                    text = "¿Qué animal es Tomás?",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro
                )

                alternativas.forEach { alternativa ->

                    val seleccionada =
                        respuestaSeleccionada == alternativa

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                enabled = !controlesBloqueados
                            ) {
                                onSeleccionarRespuesta(alternativa)
                            },
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
                                    onSeleccionarRespuesta(alternativa)
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

        // INTENTOS Y BOTÓN
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
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

        // ÉXITO: SOLO CUANDO ACIERTA
        AnimatedVisibility(
            visible = actividadAprobada,
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

        // AGOTÓ LOS INTENTOS SIN ACERTAR
        AnimatedVisibility(
            visible = intentosAgotados,
            enter = fadeIn() + expandVertically()
        ) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RosaSuave,
                shape = RoundedCornerShape(18.dp)
            ) {

                Text(
                    text = "No quedan intentos disponibles.",
                    modifier = Modifier.padding(18.dp),
                    color = RojoError,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        // MENSAJE DE RETROALIMENTACIÓN
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

        Text(
            text = "Actividad ficticia de demostración",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )
    }
}

// CONEXIÓN CON EL VIEWMODEL
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
            estudianteViewModel.comprobarRespuestaLectura()
        },

        mensaje = estadoLectura.mensaje,
        esCorrecta = estadoLectura.esCorrecta,
        finalizada = estadoLectura.finalizada,
        intentos = estadoLectura.intentos,
        maxIntentos = 3,
        modifier = modifier
    )
}

// VISTA PREVIA
@Preview(showBackground = true)
@Composable
fun ActividadLecturaPreview() {

    MPAZNEBTheme {
        ActividadLecturaScreen(
            respuestaSeleccionada = "Un gato",
            intentos = 1
        )
    }
}