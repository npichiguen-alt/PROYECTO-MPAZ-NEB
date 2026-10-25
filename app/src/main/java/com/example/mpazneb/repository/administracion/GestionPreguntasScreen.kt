
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.repository.administracion.PreguntaRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun GestionPreguntasScreen(
    actividades: List<Actividad>
) {

    val contexto = LocalContext.current.applicationContext

    // Conectar con la misma base de datos de actividades
    val repositorio = remember(contexto) {
        PreguntaRepository(
            ActividadDatabase
                .obtenerInstancia(contexto)
                .preguntaDao()
        )
    }

    val scope = rememberCoroutineScope()

    // Actividad seleccionada
    var actividadIdSeleccionada by rememberSaveable {
        mutableIntStateOf(0)
    }

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    // Campos de la pregunta
    var enunciado by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    var alternativaA by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    var alternativaB by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    var alternativaC by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    var alternativaD by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    var respuestaCorrecta by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("A")
    }

    var retroalimentacion by rememberSaveable(actividadIdSeleccionada) {
        mutableStateOf("")
    }

    // Preguntas recuperadas de Room
    var preguntas by remember {
        mutableStateOf<List<PreguntaEntity>>(emptyList())
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var cargandoPreguntas by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    // Seleccionar automáticamente la primera actividad
    LaunchedEffect(actividades) {

        if (actividades.none {
                it.id == actividadIdSeleccionada
            }) {

            actividadIdSeleccionada =
                actividades.firstOrNull()?.id ?: 0
        }
    }

    val actividadSeleccionada =
        actividades.firstOrNull {
            it.id == actividadIdSeleccionada
        }

    // Recuperar las preguntas cada vez que
    // se selecciona otra actividad
    LaunchedEffect(actividadIdSeleccionada) {

        preguntas = emptyList()
        mensaje = ""

        if (actividadIdSeleccionada != 0) {

            cargandoPreguntas = true

            try {

                preguntas = repositorio.listarPreguntas(
                    actividadIdSeleccionada
                )

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje = "No se pudieron cargar las preguntas."

            } finally {

                cargandoPreguntas = false
            }
        }
    }

    val formularioValido =
        enunciado.isNotBlank() &&
                alternativaA.isNotBlank() &&
                alternativaB.isNotBlank() &&
                alternativaC.isNotBlank() &&
                alternativaD.isNotBlank() &&
                retroalimentacion.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Gestionar preguntas",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Agrega preguntas a las actividades de Lectura y Matemática.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (actividades.isEmpty()) {

            Text(
                text = "Primero debes crear una actividad " +
                        "desde la sección Nueva."
            )

        } else {

            // SELECCIONAR ACTIVIDAD
            Text(
                text = "Actividad",
                style = MaterialTheme.typography.titleMedium
            )

            Box {

                OutlinedButton(
                    onClick = {
                        menuAbierto = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        actividadSeleccionada?.let {
                            "${it.titulo} (${it.modulo})"
                        } ?: "Seleccionar actividad"
                    )
                }

                DropdownMenu(
                    expanded = menuAbierto,
                    onDismissRequest = {
                        menuAbierto = false
                    }
                ) {

                    actividades.forEach { actividad ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "${actividad.titulo} " +
                                            "(${actividad.modulo})"
                                )
                            },
                            onClick = {

                                actividadIdSeleccionada =
                                    actividad.id

                                menuAbierto = false
                            }
                        )
                    }
                }
            }

            if (actividadSeleccionada != null) {

                Text(
                    text = "Curso: ${actividadSeleccionada.curso}° básico",
                    style = MaterialTheme.typography.bodySmall
                )

                HorizontalDivider()

                Text(
                    text = "Nueva pregunta",
                    style = MaterialTheme.typography.titleLarge
                )

                // ENUNCIADO
                OutlinedTextField(
                    value = enunciado,
                    onValueChange = {
                        enunciado = it
                    },
                    label = {
                        Text("Enunciado de la pregunta")
                    },
                    placeholder = {
                        Text("Ej.: ¿Cuánto es 8 + 5?")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // ALTERNATIVA A
                OutlinedTextField(
                    value = alternativaA,
                    onValueChange = {
                        alternativaA = it
                    },
                    label = {
                        Text("Alternativa A")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // ALTERNATIVA B
                OutlinedTextField(
                    value = alternativaB,
                    onValueChange = {
                        alternativaB = it
                    },
                    label = {
                        Text("Alternativa B")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // ALTERNATIVA C
                OutlinedTextField(
                    value = alternativaC,
                    onValueChange = {
                        alternativaC = it
                    },
                    label = {
                        Text("Alternativa C")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // ALTERNATIVA D
                OutlinedTextField(
                    value = alternativaD,
                    onValueChange = {
                        alternativaD = it
                    },
                    label = {
                        Text("Alternativa D")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // RESPUESTA CORRECTA
                Text(
                    text = "Selecciona la respuesta correcta",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf("A", "B", "C", "D").forEach {
                            letra ->

                        FilterChip(
                            selected = respuestaCorrecta == letra,
                            onClick = {
                                respuestaCorrecta = letra
                            },
                            label = {
                                Text(letra)
                            }
                        )
                    }
                }

                // RETROALIMENTACIÓN
                OutlinedTextField(
                    value = retroalimentacion,
                    onValueChange = {
                        retroalimentacion = it
                    },
                    label = {
                        Text("Retroalimentación")
                    },
                    placeholder = {
                        Text("Ej.: ¡Muy bien! 8 + 5 = 13.")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // GUARDAR PREGUNTA
                Button(
                    onClick = {

                        val idActividad =
                            actividadSeleccionada.id

                        guardando = true
                        mensaje = "Guardando pregunta..."

                        scope.launch {

                            try {

                                val pregunta = PreguntaEntity(
                                    actividadId = idActividad,
                                    enunciado = enunciado.trim(),
                                    alternativaA = alternativaA.trim(),
                                    alternativaB = alternativaB.trim(),
                                    alternativaC = alternativaC.trim(),
                                    alternativaD = alternativaD.trim(),
                                    respuestaCorrecta = respuestaCorrecta,
                                    retroalimentacion =
                                        retroalimentacion.trim()
                                )

                                repositorio.guardarPregunta(
                                    pregunta
                                )

                                preguntas =
                                    repositorio.listarPreguntas(
                                        idActividad
                                    )

                                // Limpiar formulario
                                enunciado = ""
                                alternativaA = ""
                                alternativaB = ""
                                alternativaC = ""
                                alternativaD = ""
                                respuestaCorrecta = "A"
                                retroalimentacion = ""

                                mensaje =
                                    "Pregunta guardada correctamente."

                            } catch (
                                error: CancellationException
                            ) {

                                throw error

                            } catch (error: Exception) {

                                mensaje =
                                    "No se pudo guardar la pregunta."

                            } finally {

                                guardando = false
                            }
                        }
                    },

                    enabled = formularioValido && !guardando,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {

                    Text(
                        if (guardando) {
                            "Guardando..."
                        } else {
                            "Guardar pregunta"
                        }
                    )
                }

                if (mensaje.isNotBlank()) {

                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                HorizontalDivider()

                // PREGUNTAS GUARDADAS
                Text(
                    text = "Preguntas guardadas: ${preguntas.size}",
                    style = MaterialTheme.typography.titleMedium
                )

                if (cargandoPreguntas) {

                    CircularProgressIndicator()

                } else if (preguntas.isEmpty()) {

                    Text(
                        text = "Esta actividad todavía no tiene preguntas."
                    )

                } else {

                    preguntas.forEachIndexed {
                            indice, pregunta ->

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {

                                Text(
                                    text = "Pregunta ${indice + 1}",
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(pregunta.enunciado)

                                Text("A. ${pregunta.alternativaA}")
                                Text("B. ${pregunta.alternativaB}")
                                Text("C. ${pregunta.alternativaC}")
                                Text("D. ${pregunta.alternativaD}")

                                Text(
                                    text = "Respuesta correcta: " +
                                            pregunta.respuestaCorrecta,
                                    color =
                                        MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = "Retroalimentación: " +
                                            pregunta.retroalimentacion
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
