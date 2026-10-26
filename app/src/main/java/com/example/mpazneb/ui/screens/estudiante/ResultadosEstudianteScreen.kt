
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.Resultado
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.ui.theme.MPAZNEBTheme

// ====================================
// COLORES DEL MÓDULO ESTUDIANTE
// ====================================

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
// 1. PANTALLA DE RESULTADOS
// ====================================

@Composable
fun ResultadosEstudianteScreen(
    resultados: List<Resultado> = emptyList(),
    respuestas: List<Respuesta> = emptyList(),
    actividad: ActividadEntity? = null,
    preguntas: List<PreguntaEntity> = emptyList(),
    esDemostracion: Boolean = false,
    modifier: Modifier = Modifier
) {

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
                text = "Mis resultados",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulOscuro
            )

            Text(
                text = "Consulta tu progreso y las respuestas de tus actividades.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )
        }

        // ====================================
        // 3. RESULTADOS FINALES
        // ====================================

        Text(
            text = "Resultados finales",
            style = MaterialTheme.typography.titleLarge,
            color = AzulOscuro
        )

        if (resultados.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Blanco
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Sin resultados todavía",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )

                    Text(
                        text = "Todavía no hay actividades completadas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto
                    )
                }
            }

        } else {

            resultados.forEach { resultado ->

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

                        // Identificación de la asignación
                        Text(
                            text = "Asignación N.º ${resultado.asignacionId}",
                            style = MaterialTheme.typography.titleLarge,
                            color = AzulOscuro
                        )

                        HorizontalDivider(
                            color = AzulSuave
                        )

                        // Aciertos
                        Text(
                            text = "Aciertos",
                            style = MaterialTheme.typography.labelLarge,
                            color = GrisTexto
                        )

                        Text(
                            text = "${resultado.aciertos} de ${resultado.totalPreguntas}",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Rosa
                        )

                        // Barra visual del resultado
                        val progreso =
                            if (resultado.totalPreguntas > 0) {
                                (
                                        resultado.aciertos.toFloat() /
                                                resultado.totalPreguntas
                                        ).coerceIn(0f, 1f)
                            } else {
                                0f
                            }

                        LinearProgressIndicator(
                            progress = { progreso },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = Rosa,
                            trackColor = RosaSuave
                        )

                        // Intentos realizados
                        Surface(
                            color = AzulSuave,
                            shape = RoundedCornerShape(12.dp)
                        ) {

                            Text(
                                text = "Intentos realizados: ${resultado.intentos}",
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 10.dp
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = AzulOscuro
                            )
                        }

                        // Observación del resultado
                        if (resultado.observacion.isNotEmpty()) {

                            Text(
                                text = "Observación",
                                style = MaterialTheme.typography.labelLarge,
                                color = Azul
                            )

                            Text(
                                text = resultado.observacion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = GrisTexto
                            )
                        }
                    }
                }
            }
        }

        // ====================================
        // 4. HISTORIAL DE RESPUESTAS
        // ====================================

        HorizontalDivider(
            color = Azul.copy(alpha = 0.25f)
        )

        Text(
            text = "Historial de respuestas",
            style = MaterialTheme.typography.titleLarge,
            color = AzulOscuro
        )

        if (respuestas.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Blanco
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Sin respuestas registradas",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )

                    Text(
                        text = "Todavía no hay respuestas registradas.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto
                    )
                }
            }

        } else {

            // ====================================
            // 5. RESUMEN DE RESPUESTAS
            // ====================================

            val respuestasCorrectas =
                respuestas.count { it.esCorrecta }

            val respuestasIncorrectas =
                respuestas.count { !it.esCorrecta }

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
                        text = "Resumen de respuestas",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = AzulSuave,
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(5.dp)
                            ) {

                                Text(
                                    text = "$respuestasCorrectas",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Azul
                                )

                                Text(
                                    text = "Correctas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AzulOscuro
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            color = RosaSuave,
                            shape = RoundedCornerShape(14.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(5.dp)
                            ) {

                                Text(
                                    text = "$respuestasIncorrectas",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Rosa
                                )

                                Text(
                                    text = "Incorrectas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = AzulOscuro
                                )
                            }
                        }
                    }
                }
            }

            // ====================================
            // 6. DETALLE DE CADA RESPUESTA
            // ====================================

            respuestas.forEach { respuesta ->

                // Identificar correctamente la actividad.
                val nombreActividad = when {
                    actividad != null &&
                            actividad.id == respuesta.actividadId ->
                        "${actividad.modulo}: ${actividad.titulo}"

                    esDemostracion &&
                            respuesta.actividadId == 1 ->
                        "Lectura"

                    esDemostracion &&
                            respuesta.actividadId == 2 ->
                        "Matemática"

                    else ->
                        "Actividad ${respuesta.actividadId}"
                }

// Buscar la pregunta real guardada en Room.
                val preguntaReal = preguntas.firstOrNull {
                    it.id == respuesta.preguntaId &&
                            it.actividadId == respuesta.actividadId
                }

// Obtener el número real de la pregunta
// dentro de la actividad, no su ID de Room.
                val numeroPregunta = if (preguntaReal != null) {
                    preguntas.indexOfFirst {
                        it.id == preguntaReal.id
                    } + 1
                } else {
                    respuesta.preguntaId
                }

// Mostrar el texto completo de la alternativa.
                val respuestaMostrada = when (
                    respuesta.respuestaSeleccionada.trim().uppercase()
                ) {
                    "A" -> preguntaReal?.let {
                        "A. ${it.alternativaA}"
                    } ?: respuesta.respuestaSeleccionada

                    "B" -> preguntaReal?.let {
                        "B. ${it.alternativaB}"
                    } ?: respuesta.respuestaSeleccionada

                    "C" -> preguntaReal?.let {
                        "C. ${it.alternativaC}"
                    } ?: respuesta.respuestaSeleccionada

                    "D" -> preguntaReal?.let {
                        "D. ${it.alternativaD}"
                    } ?: respuesta.respuestaSeleccionada

                    else -> respuesta.respuestaSeleccionada
                }

                val textoResultado =
                    if (respuesta.esCorrecta) {
                        "Correcta"
                    } else {
                        "Incorrecta"
                    }

                val colorResultado =
                    if (respuesta.esCorrecta) {
                        Azul
                    } else {
                        RojoError
                    }

                val fondoResultado =
                    if (respuesta.esCorrecta) {
                        AzulSuave
                    } else {
                        RosaSuave
                    }

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
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        // Nombre de la actividad
                        Text(
                            text = nombreActividad,
                            style = MaterialTheme.typography.titleLarge,
                            color = AzulOscuro
                        )

                        HorizontalDivider(
                            color = AzulSuave
                        )

                        // Número de pregunta
                        Text(
                            text = "Pregunta N.º $numeroPregunta",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto
                        )

                        // Número de intento
                        Text(
                            text = "Intento N.º ${respuesta.numeroIntento}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto
                        )

                        // Respuesta seleccionada
                        Text(
                            text = "Respuesta seleccionada",
                            style = MaterialTheme.typography.labelLarge,
                            color = GrisTexto
                        )

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = AzulSuave,
                            shape = RoundedCornerShape(12.dp)
                        ) {

                            Text(
                                text = respuestaMostrada,
                                modifier = Modifier.padding(14.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = AzulOscuro
                            )
                        }

                        // Estado de la respuesta
                        Surface(
                            color = fondoResultado,
                            shape = RoundedCornerShape(50.dp)
                        ) {

                            Text(
                                text = textoResultado,
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                color = colorResultado,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }

    }
}

// ====================================
// 8. VISTA PREVIA
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
