
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

    val repositorio = remember(contexto) {
        PreguntaRepository(
            ActividadDatabase
                .obtenerInstancia(contexto)
                .preguntaDao(),
            context = contexto
        )
    }

    val scope = rememberCoroutineScope()

    // ====================================
    // 1. SELECCIÓN DE ACTIVIDAD
    // ====================================

    var actividadIdSeleccionada by rememberSaveable {
        mutableIntStateOf(0)
    }

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    val actividadSeleccionada =
        actividades.firstOrNull {
            it.id == actividadIdSeleccionada
        }

    // Para evitar cambiar preguntas mientras
    // la actividad está disponible para estudiantes.
    val puedeModificar =
        actividadSeleccionada?.activa == false

    // ====================================
    // 2. FORMULARIO DE PREGUNTAS
    // ====================================

    var enunciado by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    var alternativaA by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    var alternativaB by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    var alternativaC by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    var alternativaD by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    var respuestaCorrecta by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("A")
    }

    var retroalimentacion by rememberSaveable(
        actividadIdSeleccionada
    ) {
        mutableStateOf("")
    }

    // Si es null, estamos creando una pregunta.
    // Si no es null, estamos editando una existente.
    var preguntaEnEdicion by remember(
        actividadIdSeleccionada
    ) {
        mutableStateOf<PreguntaEntity?>(null)
    }

    var preguntaAEliminar by remember(
        actividadIdSeleccionada
    ) {
        mutableStateOf<PreguntaEntity?>(null)
    }

    // ====================================
    // 3. ESTADOS DE ROOM
    // ====================================

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

    // ====================================
    // 4. LIMPIAR FORMULARIO
    // ====================================

    fun limpiarFormulario() {
        preguntaEnEdicion = null

        enunciado = ""
        alternativaA = ""
        alternativaB = ""
        alternativaC = ""
        alternativaD = ""

        respuestaCorrecta = "A"
        retroalimentacion = ""
    }

    // ====================================
    // 5. SELECCIONAR ACTIVIDAD INICIAL
    // ====================================

    LaunchedEffect(actividades) {

        if (
            actividades.none {
                it.id == actividadIdSeleccionada
            }
        ) {
            actividadIdSeleccionada =
                actividades.firstOrNull()?.id ?: 0
        }
    }

    // ====================================
    // 6. CARGAR PREGUNTAS DESDE ROOM
    // ====================================

    LaunchedEffect(actividadIdSeleccionada) {

        preguntas = emptyList()
        mensaje = ""

        val idActividad = actividadIdSeleccionada

        if (idActividad != 0) {

            cargandoPreguntas = true

            try {

                preguntas = repositorio.listarPreguntas(
                    idActividad
                )

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje =
                    "No se pudieron cargar las preguntas."

            } finally {

                cargandoPreguntas = false
            }
        }
    }

    // ====================================
    // 7. VALIDAR FORMULARIO
    // ====================================

    val formularioValido =
        enunciado.isNotBlank() &&
                alternativaA.isNotBlank() &&
                alternativaB.isNotBlank() &&
                alternativaC.isNotBlank() &&
                alternativaD.isNotBlank() &&
                respuestaCorrecta in listOf("A", "B", "C", "D") &&
                retroalimentacion.isNotBlank()

    // ====================================
    // 8. PANTALLA PRINCIPAL
    // ====================================

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
            text = "Crea, consulta y corrige las preguntas " +
                    "de las actividades de Lectura y Matemática."
        )

        if (actividades.isEmpty()) {

            Text(
                text = "Primero debes crear una actividad " +
                        "desde la sección Nueva."
            )

        } else {

            // ====================================
            // SELECCIONAR ACTIVIDAD
            // ====================================

            Text(
                text = "Selecciona una actividad",
                style = MaterialTheme.typography.titleMedium
            )

            Box {

                OutlinedButton(
                    onClick = {
                        menuAbierto = true
                    },
                    enabled = !guardando,
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
                    text =
                        "Curso: ${actividadSeleccionada.curso}° básico"
                )

                Text(
                    text = if (actividadSeleccionada.activa) {
                        "Estado: Activa"
                    } else {
                        "Estado: Borrador"
                    }
                )

                // ====================================
                // PROTECCIÓN DE ACTIVIDADES ACTIVAS
                // ====================================

                if (!puedeModificar) {

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme
                                    .secondaryContainer
                        )
                    ) {

                        Text(
                            text = "Para agregar, editar o eliminar " +
                                    "preguntas, primero desactiva " +
                                    "esta actividad desde su módulo.",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                HorizontalDivider()

                // ====================================
                // FORMULARIO
                // ====================================

                Text(
                    text = if (preguntaEnEdicion == null) {
                        "Nueva pregunta"
                    } else {
                        "Editar pregunta"
                    },
                    style = MaterialTheme.typography.titleLarge
                )

                if (preguntaEnEdicion != null) {

                    Text(
                        text = "Estás modificando una pregunta " +
                                "que ya existe en Room."
                    )

                    OutlinedButton(
                        onClick = {
                            limpiarFormulario()
                            mensaje = ""
                        },
                        enabled = !guardando
                    ) {
                        Text("Cancelar edición")
                    }
                }

                OutlinedTextField(
                    value = enunciado,
                    onValueChange = {
                        enunciado = it
                    },
                    label = {
                        Text("Enunciado de la pregunta")
                    },
                    minLines = 2,
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alternativaA,
                    onValueChange = {
                        alternativaA = it
                    },
                    label = {
                        Text("Alternativa A")
                    },
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alternativaB,
                    onValueChange = {
                        alternativaB = it
                    },
                    label = {
                        Text("Alternativa B")
                    },
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alternativaC,
                    onValueChange = {
                        alternativaC = it
                    },
                    label = {
                        Text("Alternativa C")
                    },
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = alternativaD,
                    onValueChange = {
                        alternativaD = it
                    },
                    label = {
                        Text("Alternativa D")
                    },
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Respuesta correcta",
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
                            selected =
                                respuestaCorrecta == letra,

                            onClick = {
                                respuestaCorrecta = letra
                            },

                            enabled =
                                puedeModificar && !guardando,

                            label = {
                                Text(letra)
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = retroalimentacion,
                    onValueChange = {
                        retroalimentacion = it
                    },
                    label = {
                        Text("Retroalimentación pedagógica")
                    },
                    minLines = 2,
                    enabled = puedeModificar && !guardando,
                    modifier = Modifier.fillMaxWidth()
                )

                // ====================================
                // GUARDAR O EDITAR PREGUNTA
                // ====================================

                Button(
                    onClick = {

                        val idActividad =
                            actividadSeleccionada.id

                        val preguntaAnterior =
                            preguntaEnEdicion

                        val pregunta = PreguntaEntity(
                            id = preguntaAnterior?.id ?: 0,
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

                        guardando = true

                        mensaje = if (
                            preguntaAnterior == null
                        ) {
                            "Guardando pregunta..."
                        } else {
                            "Guardando cambios..."
                        }

                        scope.launch {

                            try {

                                // Verificar el estado actual
                                // antes de modificar Room.
                                val actividadEnRoom =
                                    ActividadDatabase
                                        .obtenerInstancia(contexto)
                                        .actividadDao()
                                        .obtenerActividades()
                                        .firstOrNull {
                                            it.id == idActividad
                                        }

                                if (actividadEnRoom == null) {

                                    mensaje =
                                        "La actividad ya no existe."

                                } else if (actividadEnRoom.activa) {

                                    mensaje =
                                        "Desactiva la actividad " +
                                                "antes de modificar preguntas."

                                } else {

                                    if (preguntaAnterior == null) {

                                        repositorio.guardarPregunta(
                                            pregunta
                                        )

                                        mensaje =
                                            "Pregunta creada correctamente."

                                    } else {

                                        val actualizado =
                                            repositorio.editarPregunta(
                                                pregunta
                                            )

                                        mensaje =
                                            if (actualizado) {
                                                "Pregunta editada correctamente."
                                            } else {
                                                "No se encontró la pregunta."
                                            }
                                    }

                                    if (
                                        actividadIdSeleccionada ==
                                        idActividad
                                    ) {

                                        preguntas =
                                            repositorio.listarPreguntas(
                                                idActividad
                                            )

                                        limpiarFormulario()
                                    }
                                }

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

                    enabled =
                        formularioValido &&
                                puedeModificar &&
                                !guardando &&
                                !cargandoPreguntas,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {

                    Text(
                        when {
                            guardando ->
                                "Guardando..."

                            preguntaEnEdicion != null ->
                                "Guardar cambios"

                            else ->
                                "Guardar pregunta"
                        }
                    )
                }

                if (mensaje.isNotBlank()) {

                    Text(
                        text = mensaje,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider()

                // ====================================
                // PREGUNTAS REGISTRADAS
                // ====================================

                Text(
                    text = "Preguntas guardadas: ${preguntas.size}",
                    style = MaterialTheme.typography.titleLarge
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
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                Text(
                                    text = "Pregunta ${indice + 1}",
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Text(pregunta.enunciado)

                                Text(
                                    text = "A. ${pregunta.alternativaA}"
                                )

                                Text(
                                    text = "B. ${pregunta.alternativaB}"
                                )

                                Text(
                                    text = "C. ${pregunta.alternativaC}"
                                )

                                Text(
                                    text = "D. ${pregunta.alternativaD}"
                                )

                                Text(
                                    text = "Respuesta correcta: " +
                                            pregunta.respuestaCorrecta
                                )

                                Text(
                                    text = "Retroalimentación: " +
                                            pregunta.retroalimentacion
                                )

                                HorizontalDivider()

                                // ====================================
                                // EDITAR PREGUNTA
                                // ====================================

                                OutlinedButton(
                                    onClick = {

                                        preguntaEnEdicion = pregunta

                                        enunciado =
                                            pregunta.enunciado

                                        alternativaA =
                                            pregunta.alternativaA

                                        alternativaB =
                                            pregunta.alternativaB

                                        alternativaC =
                                            pregunta.alternativaC

                                        alternativaD =
                                            pregunta.alternativaD

                                        respuestaCorrecta =
                                            pregunta.respuestaCorrecta

                                        retroalimentacion =
                                            pregunta.retroalimentacion

                                        mensaje =
                                            "Editando pregunta ${indice + 1}. " +
                                                    "Modifica el formulario " +
                                                    "de la parte superior."
                                    },

                                    enabled =
                                        puedeModificar && !guardando,

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Editar pregunta")
                                }

                                // ====================================
                                // ELIMINAR PREGUNTA
                                // ====================================

                                OutlinedButton(
                                    onClick = {
                                        preguntaAEliminar = pregunta
                                    },

                                    enabled =
                                        puedeModificar && !guardando,

                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Eliminar pregunta")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ====================================
    // 9. CONFIRMAR ELIMINACIÓN
    // ====================================

    val pendiente = preguntaAEliminar

    if (pendiente != null) {

        AlertDialog(
            onDismissRequest = {
                preguntaAEliminar = null
            },

            title = {
                Text("Eliminar pregunta")
            },

            text = {
                Text(
                    "¿Seguro que deseas eliminar esta pregunta?\n\n" +
                            pendiente.enunciado +
                            "\n\nEsta acción no se puede deshacer."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        preguntaAEliminar = null
                        guardando = true

                        val idActividad = pendiente.actividadId

                        scope.launch {

                            try {

                                val actividadEnRoom =
                                    ActividadDatabase
                                        .obtenerInstancia(contexto)
                                        .actividadDao()
                                        .obtenerActividades()
                                        .firstOrNull {
                                            it.id == idActividad
                                        }

                                if (actividadEnRoom == null) {

                                    mensaje =
                                        "La actividad ya no existe."

                                } else if (actividadEnRoom.activa) {

                                    mensaje =
                                        "Desactiva la actividad " +
                                                "antes de eliminar preguntas."

                                } else {

                                    val eliminada =
                                        repositorio.eliminarPregunta(
                                            pendiente.id
                                        )

                                    mensaje =
                                        if (eliminada) {
                                            "Pregunta eliminada correctamente."
                                        } else {
                                            "No se encontró la pregunta."
                                        }

                                    if (
                                        actividadIdSeleccionada ==
                                        idActividad
                                    ) {

                                        preguntas =
                                            repositorio.listarPreguntas(
                                                idActividad
                                            )

                                        if (
                                            preguntaEnEdicion?.id ==
                                            pendiente.id
                                        ) {
                                            limpiarFormulario()
                                        }
                                    }
                                }

                            } catch (
                                error: CancellationException
                            ) {

                                throw error

                            } catch (error: Exception) {

                                mensaje =
                                    "No se pudo eliminar la pregunta."

                            } finally {

                                guardando = false
                            }
                        }
                    },

                    enabled = !guardando
                ) {

                    Text("Eliminar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        preguntaAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
