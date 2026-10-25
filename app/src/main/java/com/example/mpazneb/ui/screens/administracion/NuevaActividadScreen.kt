
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NuevaActividadScreen(
    viewModel: AdministracionViewModel
) {

    var titulo by rememberSaveable {
        mutableStateOf("")
    }

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var objetivo by rememberSaveable {
        mutableStateOf("")
    }

    var instrucciones by rememberSaveable {
        mutableStateOf("")
    }

    var modulo by rememberSaveable {
        mutableStateOf("Lectura")
    }

    var curso by rememberSaveable {
        mutableIntStateOf(1)
    }

    var dificultad by rememberSaveable {
        mutableStateOf("Básica")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Nueva actividad",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Completa los datos pedagógicos de la actividad.",
            style = MaterialTheme.typography.bodyMedium
        )

        // TÍTULO
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = {
                Text("Título de la actividad")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // DESCRIPCIÓN
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = {
                Text("Descripción")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        // NUEVO: OBJETIVO DE APRENDIZAJE
        OutlinedTextField(
            value = objetivo,
            onValueChange = { objetivo = it },
            label = {
                Text("Objetivo de aprendizaje")
            },
            placeholder = {
                Text("Ej.: Resolver sumas hasta 20")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        // NUEVO: INSTRUCCIONES
        OutlinedTextField(
            value = instrucciones,
            onValueChange = { instrucciones = it },
            label = {
                Text("Instrucciones para el estudiante")
            },
            placeholder = {
                Text("Ej.: Lee y selecciona una respuesta")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // MÓDULO
        Text(
            text = "Módulo",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = modulo == "Lectura",
                onClick = {
                    modulo = "Lectura"
                },
                label = {
                    Text("Lectura")
                }
            )

            FilterChip(
                selected = modulo == "Matemática",
                onClick = {
                    modulo = "Matemática"
                },
                label = {
                    Text("Matemática")
                }
            )
        }

        // CURSO
        Text(
            text = "Curso",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        // GUARDAR ACTIVIDAD
        Button(
            onClick = {

                viewModel.guardarActividad(
                    titulo = titulo,
                    descripcion = descripcion,
                    modulo = modulo,
                    curso = curso,
                    dificultad = dificultad,
                    objetivo = objetivo,
                    instrucciones = instrucciones
                )
            },

            enabled =
                titulo.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        objetivo.isNotBlank() &&
                        instrucciones.isNotBlank() &&
                        !viewModel.guardando,

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {

            Text(
                if (viewModel.guardando) {
                    "Guardando..."
                } else {
                    "Guardar actividad"
                }
            )
        }

        // MENSAJE DEL VIEWMODEL
        if (viewModel.mensaje.isNotBlank()) {

            Text(
                text = viewModel.mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NuevaActividadPreview() {

    val viewModel = remember {
        AdministracionViewModel()
    }

    MaterialTheme {
        NuevaActividadScreen(
            viewModel = viewModel
        )
    }
}
