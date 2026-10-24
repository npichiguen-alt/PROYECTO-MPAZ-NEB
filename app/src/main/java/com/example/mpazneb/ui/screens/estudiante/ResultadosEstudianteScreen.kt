
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.Resultado
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun ResultadosEstudianteScreen(
    resultados: List<Resultado> = emptyList(),
    respuestas: List<Respuesta> = emptyList(),
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ====================================
        // ENCABEZADO
        // ====================================

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mis resultados",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Consulta tu progreso y las respuestas de tus actividades."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ====================================
        // RESULTADOS FINALES
        // ====================================

        Text(
            text = "Resultados finales",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (resultados.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Todavía no hay actividades completadas.",
                    modifier = Modifier.padding(20.dp)
                )
            }

        } else {

            resultados.forEach { resultado ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Asignación N.º ${resultado.asignacionId}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aciertos: ${resultado.aciertos} " +
                                    "de ${resultado.totalPreguntas}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Intentos: ${resultado.intentos}"
                        )

                        if (resultado.observacion.isNotEmpty()) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = resultado.observacion
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // ====================================
        // HISTORIAL DE RESPUESTAS
        // ====================================

        Text(
            text = "Historial de respuestas",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (respuestas.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Todavía no hay respuestas registradas.",
                    modifier = Modifier.padding(20.dp)
                )
            }

        } else {

            // Contar respuestas correctas e incorrectas
            val respuestasCorrectas =
                respuestas.count { it.esCorrecta }

            val respuestasIncorrectas =
                respuestas.count { !it.esCorrecta }

            Text(
                text = "Correctas: $respuestasCorrectas  |  " +
                        "Incorrectas: $respuestasIncorrectas",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            respuestas.forEach { respuesta ->

                // Identificar la actividad de demostración
                val nombreActividad = when (
                    respuesta.actividadId
                ) {

                    1 -> "Lectura"

                    2 -> "Matemática"

                    else -> "Actividad ${respuesta.actividadId}"
                }

                // Determinar si fue correcta
                val textoResultado = if (
                    respuesta.esCorrecta
                ) {
                    "Correcta ✓"
                } else {
                    "Incorrecta ✗"
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = nombreActividad,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Pregunta N.º ${respuesta.preguntaId}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Intento N.º ${respuesta.numeroIntento}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Respuesta: ${respuesta.respuestaSeleccionada}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Resultado: $textoResultado",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Datos ficticios para uso académico",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ====================================
// VISTA PREVIA
// ====================================

@Preview(showBackground = true)
@Composable
fun ResultadosEstudiantePreview() {

    val resultadosPrueba = listOf(
        Resultado(
            id = 1,
            asignacionId = 1,
            aciertos = 1,
            totalPreguntas = 1,
            intentos = 2,
            observacion = "Actividad de Lectura (demostración)"
        )
    )

    val respuestasPrueba = listOf(
        Respuesta(
            id = 1,
            asignacionId = 1,
            actividadId = 1,
            preguntaId = 1,
            respuestaSeleccionada = "Un perro",
            esCorrecta = false,
            numeroIntento = 1,
            fechaRespuesta = System.currentTimeMillis()
        ),
        Respuesta(
            id = 2,
            asignacionId = 1,
            actividadId = 1,
            preguntaId = 1,
            respuestaSeleccionada = "Un gato",
            esCorrecta = true,
            numeroIntento = 2,
            fechaRespuesta = System.currentTimeMillis()
        )
    )

    MPAZNEBTheme {

        ResultadosEstudianteScreen(
            resultados = resultadosPrueba,
            respuestas = respuestasPrueba
        )
    }
}
