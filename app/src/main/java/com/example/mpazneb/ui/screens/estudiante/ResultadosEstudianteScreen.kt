package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.model.Resultado
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
private val VerdeExito = Color(0xFF2E7D32)
private val VerdeSuave = Color(0xFFE8F5E9)

// ====================================
// 1. INDICADOR CIRCULAR DE PROGRESO
// ====================================

@Composable
fun ProgresoCircularResultados(
    porcentaje: Float,
    color: Color = Rosa,
    trackColor: Color = RosaSuave,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(130.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val sweepAngle = (porcentaje.coerceIn(0f, 1f)) * 360f

            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(porcentaje.coerceIn(0f, 1f) * 100).toInt()}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )
            Text(
                text = "Logro",
                style = MaterialTheme.typography.labelSmall,
                color = GrisTexto
            )
        }
    }
}

// ====================================
// 2. TARJETA DE ESTADÍSTICA
// ====================================

@Composable
fun EstadisticaCard(
    titulo: String,
    valor: String,
    icono: String,
    colorFondo: Color,
    colorTexto: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = colorFondo,
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = icono,
                fontSize = 22.sp
            )
            Column {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorTexto
                )
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorTexto.copy(alpha = 0.85f)
                )
            }
        }
    }
}

// ====================================
// 3. TARJETA DESPLEGABLE DE HISTORIAL DE PREGUNTA
// ====================================

@Composable
fun HistorialPreguntaCard(
    numeroPregunta: Int,
    pregunta: PreguntaEntity?,
    respuestasPregunta: List<Respuesta>
) {
    var expandido by remember { mutableStateOf(false) }

    val fueAcertada = respuestasPregunta.any { it.esCorrecta }
    val intentosUsados = respuestasPregunta.maxOfOrNull { it.numeroIntento } ?: 0
    val finalizada = fueAcertada || intentosUsados >= 3

    val (estadoTexto, estadoFondo, estadoTextoColor) = when {
        fueAcertada -> Triple("Acertada", VerdeSuave, VerdeExito)
        intentosUsados >= 3 -> Triple("Intentos agotados", Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Triple("Pendiente", AzulSuave, Azul)
    }

    val enunciadoTexto = pregunta?.enunciado ?: "Pregunta N.º $numeroPregunta"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandido = !expandido },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pregunta N.º $numeroPregunta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulOscuro
                        )
                        Surface(
                            color = estadoFondo,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = estadoTexto,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = estadoTextoColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = enunciadoTexto,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto,
                        maxLines = if (expandido) Int.MAX_VALUE else 1
                    )
                }

                Text(
                    text = if (expandido) "▲" else "▼",
                    style = MaterialTheme.typography.titleMedium,
                    color = Rosa,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            AnimatedVisibility(visible = expandido) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(color = AzulSuave)

                    if (respuestasPregunta.isEmpty()) {
                        Text(
                            text = "Aún no has respondido esta pregunta.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto
                        )
                    } else {
                        respuestasPregunta.forEach { resp ->
                            val alternativaTexto = when (resp.respuestaSeleccionada.trim().uppercase()) {
                                "A" -> pregunta?.let { "A. ${it.alternativaA}" } ?: "Alternativa A"
                                "B" -> pregunta?.let { "B. ${it.alternativaB}" } ?: "Alternativa B"
                                "C" -> pregunta?.let { "C. ${it.alternativaC}" } ?: "Alternativa C"
                                "D" -> pregunta?.let { "D. ${it.alternativaD}" } ?: "Alternativa D"
                                else -> resp.respuestaSeleccionada
                            }

                            val colorResultado = if (resp.esCorrecta) VerdeExito else Color(0xFFC62828)
                            val fondoResultado = if (resp.esCorrecta) VerdeSuave else Color(0xFFFFEBEE)

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Intento N.º ${resp.numeroIntento}",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = GrisTexto
                                        )
                                        Text(
                                            text = alternativaTexto,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = AzulOscuro
                                        )
                                    }

                                    Surface(
                                        color = fondoResultado,
                                        shape = RoundedCornerShape(50.dp)
                                    ) {
                                        Text(
                                            text = if (resp.esCorrecta) "Correcta" else "Incorrecta",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = colorResultado
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (finalizada && !fueAcertada && pregunta != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Respuesta correcta: ${pregunta.respuestaCorrecta}",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                                if (pregunta.retroalimentacion.isNotBlank()) {
                                    Text(
                                        text = pregunta.retroalimentacion,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTexto
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ====================================
// 4. PANTALLA PRINCIPAL DE RESULTADOS
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

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "MPAZ NEB",
                style = MaterialTheme.typography.titleMedium,
                color = Rosa
            )

            Text(
                text = "Mis resultados",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulOscuro,
                fontWeight = FontWeight.Bold
            )

            val nombreActividad = actividad?.titulo ?: "Actividad asignada"
            Text(
                text = "Informe de logro: $nombreActividad",
                style = MaterialTheme.typography.bodyLarge,
                color = GrisTexto
            )
        }

        // RESULTADOS PRINCIPALES
        val resultadoPrincipal = resultados.firstOrNull()

        if (resultadoPrincipal == null && respuestas.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📖",
                        fontSize = 40.sp
                    )
                    Text(
                        text = "Sin actividades completadas",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aún no has registrado respuestas en esta actividad. ¡Ingresa tu código y comienza a aprender!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto,
                        textAlign = TextAlign.Center
                    )
                }
            }

        } else {

            val totalPreguntas = resultadoPrincipal?.totalPreguntas
                ?: preguntas.size.coerceAtLeast(1)

            val aciertos = resultadoPrincipal?.aciertos
                ?: preguntas.count { p -> respuestas.any { r -> r.preguntaId == p.id && r.esCorrecta } }

            val totalIntentos = resultadoPrincipal?.intentos
                ?: respuestas.size

            val porcentaje = if (totalPreguntas > 0) aciertos.toFloat() / totalPreguntas else 0f

            val mensajeEducativo = when {
                porcentaje >= 1.0f -> "¡Excelente trabajo! Has logrado un puntaje perfecto. 🎉"
                porcentaje >= 0.7f -> "¡Muy buen trabajo! Has demostrado un excelente avance. 🌟"
                else -> "¡Buen esfuerzo! Revisa tus respuestas para seguir mejorando. 💪"
            }

            // TARJETA PRINCIPAL CON INDICADOR CIRCULAR
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Resumen de Logro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )

                    ProgresoCircularResultados(
                        porcentaje = porcentaje,
                        color = if (porcentaje >= 0.7f) Rosa else Azul
                    )

                    Text(
                        text = "$aciertos de $totalPreguntas preguntas acertadas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AzulOscuro
                    )

                    Surface(
                        color = if (porcentaje >= 0.7f) RosaSuave else AzulSuave,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = mensajeEducativo,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = AzulOscuro,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // TARJETAS DE ESTADÍSTICAS
            val preguntasPendientes = (totalPreguntas - aciertos).coerceAtLeast(0)
            val respuestasCorrectas = respuestas.count { it.esCorrecta }
            val respuestasIncorrectas = respuestas.count { !it.esCorrecta }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Estadísticas detalladas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EstadisticaCard(
                        titulo = "Preguntas acertadas",
                        valor = "$aciertos",
                        icono = "",
                        colorFondo = VerdeSuave,
                        colorTexto = VerdeExito,
                        modifier = Modifier.weight(1f)
                    )

                    EstadisticaCard(
                        titulo = "Por reforzar",
                        valor = "$preguntasPendientes",
                        icono = "",
                        colorFondo = RosaSuave,
                        colorTexto = Rosa,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    EstadisticaCard(
                        titulo = "Respuestas correctas",
                        valor = "$respuestasCorrectas",
                        icono = " ",
                        colorFondo = AzulSuave,
                        colorTexto = Azul,
                        modifier = Modifier.weight(1f)
                    )

                    EstadisticaCard(
                        titulo = "Respuestas incorrectas",
                        valor = "$respuestasIncorrectas",
                        icono = "",
                        colorFondo = Color(0xFFFFEBEE),
                        colorTexto = Color(0xFFC62828),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // OBSERVACIÓN PEDAGÓGICA (Si existe)
            if (resultadoPrincipal?.observacion?.isNotBlank() == true) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "💬", fontSize = 20.sp)
                            Text(
                                text = "Observación pedagógica",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Azul
                            )
                        }
                        Text(
                            text = resultadoPrincipal.observacion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrisTexto
                        )
                    }
                }
            }

            // HISTORIAL DESPLEGABLE DE PREGUNTAS
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Historial de preguntas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AzulOscuro
                )

                if (preguntas.isNotEmpty()) {
                    preguntas.forEachIndexed { index, preg ->
                        val respuestasDePregunta = respuestas.filter {
                            it.actividadId == (actividad?.id ?: it.actividadId) &&
                                    it.preguntaId == preg.id
                        }

                        HistorialPreguntaCard(
                            numeroPregunta = index + 1,
                            pregunta = preg,
                            respuestasPregunta = respuestasDePregunta
                        )
                    }
                } else if (respuestas.isNotEmpty()) {
                    // Si no hay lista de preguntas pero hay respuestas registradas
                    val preguntasIds = respuestas.map { it.preguntaId }.distinct()
                    preguntasIds.forEachIndexed { index, pId ->
                        val respuestasDePregunta = respuestas.filter { it.preguntaId == pId }
                        HistorialPreguntaCard(
                            numeroPregunta = index + 1,
                            pregunta = null,
                            respuestasPregunta = respuestasDePregunta
                        )
                    }
                }
            }
        }
    }
}

// ====================================
// 5. VISTA PREVIA
// ====================================

@Preview(showBackground = true)
@Composable
fun ResultadosEstudiantePreview() {

    val resultadosPrueba = listOf(
        Resultado(
            id = 1,
            asignacionId = 1,
            aciertos = 1,
            totalPreguntas = 2,
            intentos = 3,
            observacion = "¡Excelente avance en lectura!"
        )
    )

    val respuestasPrueba = listOf(
        Respuesta(
            id = 1,
            asignacionId = 1,
            actividadId = 1,
            preguntaId = 1,
            respuestaSeleccionada = "A",
            esCorrecta = false,
            numeroIntento = 1,
            fechaRespuesta = System.currentTimeMillis()
        ),
        Respuesta(
            id = 2,
            asignacionId = 1,
            actividadId = 1,
            preguntaId = 1,
            respuestaSeleccionada = "B",
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
