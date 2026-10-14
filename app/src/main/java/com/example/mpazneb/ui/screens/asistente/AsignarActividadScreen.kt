
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.FormularioAsignacion
import com.example.mpazneb.model.asistente.ValidadorAsignacion
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun AsignarActividadScreen(
    formulario: FormularioAsignacion,
    actividades: List<ActividadAula>,
    estudiantes: List<EstudianteAula>,
    trabajando: Boolean,
    onModuloChange: (String) -> Unit,
    onCursoChange: (Int) -> Unit,
    onDificultadChange: (String) -> Unit,
    onActividadChange: (Int) -> Unit,
    onEstudianteChange: (String) -> Unit,
    onAsignar: () -> Unit
) {

    // CONTROLA LA VENTANA DE CONFIRMACION
    var mostrarConfirmacion by rememberSaveable {
        mutableStateOf(false)
    }

    // MENSAJE DE ERROR DEL FORMULARIO
    var errorFormulario by rememberSaveable {
        mutableStateOf("")
    }

    // ACTIVIDADES DISPONIBLES SEGUN LOS FILTROS
    val actividadesDisponibles = actividades.filter {
        it.estado == "Disponible" &&
                it.modulo == formulario.modulo &&
                it.curso == formulario.curso &&
                it.dificultad == formulario.dificultad
    }

    // SOLO SE PUEDE ASIGNAR A ESTUDIANTES ACTIVOS
    val estudiantesActivos = estudiantes.filter {
        it.activo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        TituloAsistente(
            titulo = "Asignar actividad",
            descripcion = "Selecciona una actividad y un estudiante ficticio."
        )

        // 1. SELECCIONAR MODULO
        Text(
            text = "1. Selecciona el módulo",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Lectura", "Matemática").forEach { modulo ->

                FilterChip(
                    selected = formulario.modulo == modulo,
                    onClick = {
                        onModuloChange(modulo)
                        errorFormulario = ""
                    },
                    enabled = !trabajando,
                    label = {
                        Text(modulo)
                    }
                )
            }
        }

        // 2. SELECCIONAR CURSO
        Text(
            text = "2. Selecciona el curso",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..4).forEach { curso ->

                FilterChip(
                    selected = formulario.curso == curso,
                    onClick = {
                        onCursoChange(curso)
                        errorFormulario = ""
                    },
                    enabled = !trabajando,
                    label = {
                        Text("${curso}° básico")
                    }
                )
            }
        }

        // 3. SELECCIONAR DIFICULTAD
        Text(
            text = "3. Selecciona la dificultad",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Básica",
                "Media",
                "Avanzada"
            ).forEach { dificultad ->

                FilterChip(
                    selected = formulario.dificultad == dificultad,
                    onClick = {
                        onDificultadChange(dificultad)
                        errorFormulario = ""
                    },
                    enabled = !trabajando,
                    label = {
                        Text(dificultad)
                    }
                )
            }
        }

        // 4. SELECCIONAR ACTIVIDAD
        Text(
            text = "4. Selecciona una actividad",
            style = MaterialTheme.typography.titleMedium
        )

        if (actividadesDisponibles.isEmpty()) {

            Text(
                text = "No hay actividades disponibles con estos filtros. Prueba otro curso o dificultad.",
                style = MaterialTheme.typography.bodyMedium
            )

        } else {

            actividadesDisponibles.forEach { actividad ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = formulario.actividadId == actividad.id,
                                enabled = !trabajando,
                                role = Role.RadioButton,
                                onClick = {
                                    onActividadChange(actividad.id)
                                    errorFormulario = ""
                                }
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RadioButton(
                            selected = formulario.actividadId == actividad.id,
                            onClick = null,
                            enabled = !trabajando
                        )

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Column {
                            Text(
                                text = actividad.titulo,
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = actividad.descripcion,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        // 5. SELECCIONAR ESTUDIANTE
        Text(
            text = "5. Selecciona un estudiante",
            style = MaterialTheme.typography.titleMedium
        )

        if (estudiantesActivos.isEmpty()) {

            Text(
                text = "No hay estudiantes activos disponibles."
            )

        } else {

            estudiantesActivos.forEach { estudiante ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = formulario.estudianteAlias == estudiante.alias,
                            enabled = !trabajando,
                            role = Role.RadioButton,
                            onClick = {
                                onEstudianteChange(estudiante.alias)
                                errorFormulario = ""
                            }
                        )
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = formulario.estudianteAlias == estudiante.alias,
                        onClick = null,
                        enabled = !trabajando
                    )

                    Text(estudiante.alias)
                }
            }
        }

        // MOSTRAR ERROR DE VALIDACION
        if (errorFormulario.isNotBlank()) {

            Text(
                text = errorFormulario,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // BOTON PARA REVISAR LA ASIGNACION
        Button(
            onClick = {

                val error = ValidadorAsignacion.validar(
                    formulario,
                    actividades,
                    estudiantes
                )

                if (error != null) {
                    errorFormulario = error
                } else {
                    errorFormulario = ""
                    mostrarConfirmacion = true
                }
            },
            enabled = !trabajando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (trabajando) "Guardando..."
                else "Revisar y asignar"
            )
        }

        Text(
            text = "Se utilizan únicamente datos ficticios para esta demostración.",
            style = MaterialTheme.typography.bodySmall
        )
    }

    // VENTANA DE CONFIRMACION
    if (mostrarConfirmacion) {

        val actividadElegida = actividades.firstOrNull {
            it.id == formulario.actividadId
        }

        AlertDialog(
            onDismissRequest = {
                mostrarConfirmacion = false
            },

            title = {
                Text("Confirmar asignación")
            },

            text = {
                Text(
                    "Actividad: ${actividadElegida?.titulo ?: "Sin actividad"}\n" +
                            "Estudiante: ${formulario.estudianteAlias}\n" +
                            "Curso: ${formulario.curso}° básico\n\n" +
                            "¿Deseas realizar esta asignación?"
                )
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                        onAsignar()
                    },
                    enabled = !trabajando
                ) {
                    Text("Confirmar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarConfirmacion = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// VISTA PREVIA PARA PROBAR EL FORMULARIO
@Preview(showBackground = true)
@Composable
fun AsignarActividadPreview() {

    var formularioDemo by remember {
        mutableStateOf(FormularioAsignacion())
    }

    val actividadesDemo = listOf(
        ActividadAula(
            id = 1,
            titulo = "Cuento del bosque",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Comprensión de lectura con texto breve.",
            estado = "Disponible"
        ),
        ActividadAula(
            id = 2,
            titulo = "Resolver sumas",
            modulo = "Matemática",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Resolver sumas sencillas.",
            estado = "Disponible"
        ),
        ActividadAula(
            id = 3,
            titulo = "Vocabulario divertido",
            modulo = "Lectura",
            curso = 3,
            dificultad = "Media",
            descripcion = "Identificar sinónimos.",
            estado = "Disponible"
        )
    )

    val estudiantesDemo = listOf(
        EstudianteAula("Estudiante A"),
        EstudianteAula("Estudiante B"),
        EstudianteAula("Estudiante C"),
        EstudianteAula("Estudiante D", false)
    )

    MPAZNEBTheme {

        AsignarActividadScreen(
            formulario = formularioDemo,
            actividades = actividadesDemo,
            estudiantes = estudiantesDemo,
            trabajando = false,

            onModuloChange = {
                formularioDemo = formularioDemo.copy(
                    modulo = it,
                    actividadId = 0
                )
            },

            onCursoChange = {
                formularioDemo = formularioDemo.copy(
                    curso = it,
                    actividadId = 0
                )
            },

            onDificultadChange = {
                formularioDemo = formularioDemo.copy(
                    dificultad = it,
                    actividadId = 0
                )
            },

            onActividadChange = {
                formularioDemo = formularioDemo.copy(
                    actividadId = it
                )
            },

            onEstudianteChange = {
                formularioDemo = formularioDemo.copy(
                    estudianteAlias = it
                )
            },

            // EN PREVIEW SOLO SIMULAMOS LA ACCION
            onAsignar = {
                formularioDemo = FormularioAsignacion()
            }
        )
    }
}
