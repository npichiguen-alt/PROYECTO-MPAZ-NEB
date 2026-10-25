
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.Actividad

@Composable
fun EditarActividadDialog(
    actividad: Actividad,
    guardando: Boolean,
    onDismiss: () -> Unit,
    onGuardar: (
        String,
        String,
        Int,
        String,
        String,
        String
    ) -> Unit
) {

    // Cargar los datos existentes
    var titulo by remember(actividad.id) {
        mutableStateOf(actividad.titulo)
    }

    var descripcion by remember(actividad.id) {
        mutableStateOf(actividad.descripcion)
    }

    var objetivo by remember(actividad.id) {
        mutableStateOf(actividad.objetivo)
    }

    var instrucciones by remember(actividad.id) {
        mutableStateOf(actividad.instrucciones)
    }

    var curso by remember(actividad.id) {
        mutableIntStateOf(actividad.curso)
    }

    var dificultad by remember(actividad.id) {
        mutableStateOf(actividad.dificultad)
    }

    val formularioValido =
        titulo.isNotBlank() &&
                descripcion.isNotBlank() &&
                objetivo.isNotBlank() &&
                instrucciones.isNotBlank()

    AlertDialog(
        onDismissRequest = {
            if (!guardando) {
                onDismiss()
            }
        },

        title = {
            Text("Editar actividad")
        },

        text = {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp)
                    .verticalScroll(rememberScrollState()),

                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Módulo: ${actividad.modulo}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = if (actividad.activa) {
                        "Estado: Activa"
                    } else {
                        "Estado: Borrador"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                // TÍTULO
                OutlinedTextField(
                    value = titulo,
                    onValueChange = {
                        titulo = it
                    },
                    label = {
                        Text("Título de la actividad")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // DESCRIPCIÓN
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = {
                        descripcion = it
                    },
                    label = {
                        Text("Descripción")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // NUEVO: OBJETIVO DE APRENDIZAJE
                OutlinedTextField(
                    value = objetivo,
                    onValueChange = {
                        objetivo = it
                    },
                    label = {
                        Text("Objetivo de aprendizaje")
                    },
                    placeholder = {
                        Text("Ej.: Comprender un cuento breve")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // NUEVO: INSTRUCCIONES
                OutlinedTextField(
                    value = instrucciones,
                    onValueChange = {
                        instrucciones = it
                    },
                    label = {
                        Text("Instrucciones para el estudiante")
                    },
                    placeholder = {
                        Text("Ej.: Lee y responde las preguntas")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                // CURSO
                Text(
                    text = "Curso",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    (1..4).forEach { numero ->

                        FilterChip(
                            selected = curso == numero,
                            onClick = {
                                curso = numero
                            },
                            label = {
                                Text("${numero}°")
                            }
                        )
                    }
                }

                // DIFICULTAD
                Text(
                    text = "Dificultad",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "Básica",
                        "Media",
                        "Avanzada"
                    ).forEach { opcion ->

                        FilterChip(
                            selected = dificultad == opcion,
                            onClick = {
                                dificultad = opcion
                            },
                            label = {
                                Text(opcion)
                            }
                        )
                    }
                }

                if (!formularioValido) {
                    Text(
                        text = "Completa todos los campos " +
                                "para guardar los cambios.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },

        confirmButton = {

            Button(
                onClick = {
                    onGuardar(
                        titulo.trim(),
                        descripcion.trim(),
                        curso,
                        dificultad,
                        objetivo.trim(),
                        instrucciones.trim()
                    )
                },
                enabled = !guardando && formularioValido
            ) {

                Text(
                    if (guardando) {
                        "Guardando..."
                    } else {
                        "Guardar cambios"
                    }
                )
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss,
                enabled = !guardando
            ) {
                Text("Cancelar")
            }
        }
    )
}
