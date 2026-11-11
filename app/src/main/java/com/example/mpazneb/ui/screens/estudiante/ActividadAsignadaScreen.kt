
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
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Mi actividad asignada",
            style = MaterialTheme.typography.headlineMedium
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleLarge
                )

                Text("Módulo: ${actividad.modulo}")

                Text("Curso: ${actividad.curso}° básico")

                Text("Dificultad: ${actividad.dificultad}")

                HorizontalDivider()

                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(actividad.descripcion)

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

                Text(
                    text = "Preguntas disponibles: $cantidadPreguntas"
                )
            }
        }

        if (cantidadPreguntas == 0) {

            Text(
                text = "Esta actividad todavía no tiene preguntas."
            )
        }

        Button(
            onClick = onAbrirActividad,
            enabled = cantidadPreguntas > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Comenzar actividad")
        }

        OutlinedButton(
            onClick = onAbrirResultados,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mis resultados")
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

            Text(
                text = "Pregunta ${indicePregunta + 1} de ${preguntas.size}",
                style = MaterialTheme.typography.titleLarge
            )

            LinearProgressIndicator(
                progress = {
                    (indicePregunta + 1).toFloat() /
                            preguntas.size.coerceAtLeast(1)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = pregunta.enunciado,
                        style = MaterialTheme.typography.titleMedium
                    )

                    // ====================================
                    // ALTERNATIVAS
                    // ====================================

                    val alternativas = listOf(
                        "A" to pregunta.alternativaA,
                        "B" to pregunta.alternativaB,
                        "C" to pregunta.alternativaC,
                        "D" to pregunta.alternativaD
                    )

                    alternativas.forEach { (letra, texto) ->

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment =
                                androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            RadioButton(
                                selected = opcionElegida == letra,
                                onClick = {
                                    opcionElegida = letra
                                },
                                enabled =
                                    !preguntaFinalizada && !guardando
                            )

                            Text(
                                text = "$letra. $texto",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // ====================================
            // INTENTOS
            // ====================================

            Text(
                text = "Intentos realizados: $intentosRealizados de 3"
            )

            if (!preguntaFinalizada) {

                Text(
                    text = "Intentos restantes: $intentosRestantes"
                )

                Button(
                    onClick = {
                        onResponder(
                            pregunta.id,
                            opcionElegida
                        )
                    },
                    enabled =
                        opcionElegida.isNotBlank() && !guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = if (guardando) {
                            "Guardando respuesta..."
                        } else {
                            "Comprobar respuesta"
                        }
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
