
package com.example.mpazneb.ui.screens.estudiante
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.model.Respuesta

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

private val Rosa = Color(0xFFEA3677)
private val Azul = Color(0xFF248ED8)
private val AzulSuave = Color(0xFFE7F5FE)
private val AzulOscuro = Color(0xFF242B57)
private val RosaSuave = Color(0xFFFFE8F1)
private val Blanco = Color.White
private val VerdeSuave = Color(0xFFE8F5E9)
private val VerdeOscuro = Color(0xFF2E7D32)

// ====================================
// INICIO DE LA ACTIVIDAD ASIGNADA
// ====================================

@Composable
fun InicioActividadAsignadaScreen(
    actividad: ActividadEntity,
    cantidadPreguntas: Int,
    onAbrirActividad: () -> Unit,
    onAbrirResultados: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.titleMedium,
            color = Rosa
        )

        Text(
            text = "Mi actividad asignada",
            style = MaterialTheme.typography.headlineMedium,
            color = AzulOscuro
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold
                )

                // Chips para Módulo, Curso y Dificultad
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = AzulSuave,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "📚 ${actividad.modulo}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = AzulOscuro
                        )
                    }

                    Surface(
                        color = RosaSuave,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "🎓 ${actividad.curso}° básico",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Rosa
                        )
                    }

                    Surface(
                        color = Color(0xFFFFF9C4),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "⭐ ${actividad.dificultad}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFF57F17)
                        )
                    }
                }

                HorizontalDivider(color = AzulSuave)

                Text(
                    text = "📝 Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    color = AzulOscuro
                )

                Text(
                    text = actividad.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF57637E)
                )

                if (actividad.objetivo.isNotBlank()) {
                    Text(
                        text = "🎯 Objetivo",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )

                    Text(
                        text = actividad.objetivo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF57637E)
                    )
                }

                if (actividad.instrucciones.isNotBlank()) {
                    Text(
                        text = "📋 Instrucciones",
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )

                    Text(
                        text = actividad.instrucciones,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF57637E)
                    )
                }

                HorizontalDivider(color = AzulSuave)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Preguntas disponibles:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF57637E)
                    )
                    Surface(
                        color = Azul,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "$cantidadPreguntas",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Blanco,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (cantidadPreguntas == 0) {
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Esta actividad todavía no tiene preguntas.",
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFFE65100)
                )
            }
        }

        Button(
            onClick = onAbrirActividad,
            enabled = cantidadPreguntas > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Rosa,
                contentColor = Blanco
            )
        ) {
            Text(
                text = "Comenzar actividad",
                style = MaterialTheme.typography.titleMedium
            )
        }

        OutlinedButton(
            onClick = onAbrirResultados,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Mis resultados",
                style = MaterialTheme.typography.titleMedium,
                color = Azul
            )
        }
    }
}

// ====================================
// RESOLVER PREGUNTAS REALES
// ====================================

@Composable
fun ActividadAsignadaScreen(
    actividad: ActividadEntity,
    preguntas: List<PreguntaEntity>,
    respuestas: List<Respuesta>,
    guardando: Boolean,
    mensaje: String,
    onResponder: (Int, String) -> Unit,
    onVerResultados: () -> Unit
) {

    // ====================================
    // CONTINUAR DESDE LA PRIMERA PREGUNTA PENDIENTE
    // ====================================

    val indicePendiente = preguntas.indexOfFirst { pregunta ->

        val historialPregunta = respuestas.filter {
            it.actividadId == actividad.id &&
                    it.preguntaId == pregunta.id
        }

        val respondidaCorrectamente =
            historialPregunta.any {
                it.esCorrecta
            }

        val intentosRealizados =
            historialPregunta.maxOfOrNull {
                it.numeroIntento
            } ?: 0

        // Pendiente: todavía no acertó
        // y le quedan intentos disponibles.

        !respondidaCorrectamente && intentosRealizados < 3
    }

    // Si terminó todas las preguntas,
    // mostrar la última para ver los resultados.

    val indiceInicial = if (indicePendiente >= 0) {
        indicePendiente
    } else {
        preguntas.lastIndex.coerceAtLeast(0)
    }

    // Recordar la posición mientras esté
    // abierta esta pantalla.
    // Al volver a entrar, recalcular desde Room.

    var indicePregunta by remember(actividad.id) {
        mutableIntStateOf(indiceInicial)
    }


    val pregunta = preguntas.getOrNull(
        indicePregunta
    )

    // La selección se reinicia cuando
    // pasamos a otra pregunta.
    var opcionElegida by rememberSaveable(
        actividad.id,
        pregunta?.id
    ) {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = actividad.modulo,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = actividad.titulo,
            style = MaterialTheme.typography.headlineMedium
        )

        if (actividad.objetivo.isNotBlank()) {

            Text(
                text = "Objetivo",
                style = MaterialTheme.typography.titleMedium
            )

            Text(actividad.objetivo)
        }

        if (actividad.instrucciones.isNotBlank()) {

            Text(
                text = "Instrucciones",
                style = MaterialTheme.typography.titleMedium
            )

            Text(actividad.instrucciones)
        }

        HorizontalDivider()

        if (pregunta == null) {

            Text(
                text = "No hay preguntas disponibles."
            )

        } else {

            // ====================================
            // HISTORIAL DE ESTA PREGUNTA
            // ====================================

            val historialPregunta =
                respuestas.filter {
                    it.actividadId == actividad.id &&
                            it.preguntaId == pregunta.id
                }

            val intentosRealizados =
                historialPregunta.maxOfOrNull {
                    it.numeroIntento
                } ?: 0

            val fueCorrecta =
                historialPregunta.any {
                    it.esCorrecta
                }

            val preguntaFinalizada =
                fueCorrecta || intentosRealizados >= 3

            val intentosRestantes =
                (3 - intentosRealizados).coerceAtLeast(0)

            // ====================================
            // ENCABEZADO
            // ====================================

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pregunta ${indicePregunta + 1} de ${preguntas.size}",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro,
                    fontWeight = FontWeight.Bold
                )

                // Indicador visual de intentos restantes con corazones
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { i ->
                        Text(
                            text = if (i < intentosRestantes) "❤️" else "🖤",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = {
                    (indicePregunta + 1).toFloat() /
                            preguntas.size.coerceAtLeast(1)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Rosa,
                trackColor = RosaSuave
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = pregunta.enunciado,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro,
                        fontWeight = FontWeight.Bold
                    )

                    // ====================================
                    // ALTERNATIVAS COMO TARJETAS SELECCIONABLES
                    // ====================================

                    val alternativas = listOf(
                        "A" to pregunta.alternativaA,
                        "B" to pregunta.alternativaB,
                        "C" to pregunta.alternativaC,
                        "D" to pregunta.alternativaD
                    )

                    alternativas.forEach { (letra, texto) ->

                        val seleccionada = opcionElegida == letra
                        val fondoColor = if (seleccionada) AzulSuave else Blanco
                        val bordeColor = if (seleccionada) Rosa else Color(0xFFE0E0E0)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = if (seleccionada) 2.dp else 1.dp,
                                    color = bordeColor,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable(
                                    enabled = !preguntaFinalizada && !guardando,
                                    onClick = { opcionElegida = letra }
                                ),
                            shape = RoundedCornerShape(16.dp),
                            color = fondoColor
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = seleccionada,
                                    onClick = { opcionElegida = letra },
                                    enabled = !preguntaFinalizada && !guardando,
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Rosa,
                                        unselectedColor = Azul
                                    )
                                )

                                Text(
                                    text = "$letra. $texto",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = AzulOscuro,
                                    fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // ====================================
            // INFORMACIÓN DE INTENTOS Y BOTÓN COMPROBAR
            // ====================================

            if (!preguntaFinalizada) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Intentos realizados: $intentosRealizados de 3",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF57637E)
                    )
                    Text(
                        text = "Restantes: $intentosRestantes",
                        style = MaterialTheme.typography.labelLarge,
                        color = Rosa,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        onResponder(
                            pregunta.id,
                            opcionElegida
                        )
                    },
                    enabled = opcionElegida.isNotBlank() && !guardando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Rosa,
                        contentColor = Blanco
                    )
                ) {

                    Text(
                        text = if (guardando) {
                            "Guardando respuesta..."
                        } else {
                            "Comprobar respuesta"
                        },
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            // ====================================
            // RETROALIMENTACIÓN DE LA PREGUNTA
            // ====================================

            // Si agotó los tres intentos,
            // mostrar la respuesta correcta.

            if (!fueCorrecta && intentosRealizados >= 3) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.errorContainer
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Has agotado tus 3 intentos."
                        )

                        val textoRespuestaCorrecta =
                            when (
                                pregunta.respuestaCorrecta
                                    .trim()
                                    .uppercase()
                            ) {
                                "A" -> pregunta.alternativaA
                                "B" -> pregunta.alternativaB
                                "C" -> pregunta.alternativaC
                                "D" -> pregunta.alternativaD
                                else -> ""
                            }

                        Text(
                            text = "Respuesta correcta: " +
                                    "${pregunta.respuestaCorrecta}. " +
                                    textoRespuestaCorrecta
                        )

                        if (pregunta.retroalimentacion.isNotBlank()) {

                            Text(
                                text = pregunta.retroalimentacion
                            )
                        }
                    }
                }
            }

            // ====================================
            // POKÉAPI: RETROALIMENTACIÓN VISUAL
            // ====================================

            val ultimoIntento =
                historialPregunta.maxByOrNull {
                    it.numeroIntento
                }

            if (ultimoIntento != null && !guardando) {

                val nombrePokemon = when {

                    ultimoIntento.esCorrecta ->
                        "pikachu"

                    intentosRealizados >= 3 ->
                        "bulbasaur"

                    else ->
                        "eevee"
                }

                val mensajePokemon = when {

                    ultimoIntento.esCorrecta ->
                        "¡Respuesta correcta! " +
                                pregunta.retroalimentacion.ifBlank {
                                    "¡Excelente trabajo!"
                                }

                    intentosRealizados >= 3 ->
                        "¡No te rindas! Cada intento te ayuda a aprender."

                    else ->
                        "¡Buen intento! Lee nuevamente y prueba otra vez."
                }

                PokemonAlResponder(
                    nombrePokemon = nombrePokemon,
                    mensaje = mensajePokemon
                )
            }




            // Mensaje de problemas al guardar.
            if (mensaje.isNotBlank()) {

                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            // ====================================
            // SIGUIENTE PREGUNTA
            // ====================================

            if (preguntaFinalizada && !guardando) {

                if (indicePregunta < preguntas.lastIndex) {

                    Button(
                        onClick = {
                            indicePregunta += 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Siguiente pregunta")
                    }

                } else {

                    val todasFinalizadas =
                        preguntas.all { item ->

                            val intentosItem =
                                respuestas.filter {
                                    it.actividadId == actividad.id &&
                                            it.preguntaId == item.id
                                }

                            val respondidaCorrectamente =
                                intentosItem.any {
                                    it.esCorrecta
                                }

                            val intentosItemRealizados =
                                intentosItem.maxOfOrNull {
                                    it.numeroIntento
                                } ?: 0

                            respondidaCorrectamente ||
                                    intentosItemRealizados >= 3
                        }

                    if (todasFinalizadas) {

                        Text(
                            text = "¡Has terminado la actividad!",
                            style = MaterialTheme.typography.titleLarge
                        )

                        Button(
                            onClick = onVerResultados,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ver mis resultados")
                        }
                    }
                }
            }
        }
    }
}
