
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

import com.example.mpazneb.ui.viewmodel.AdministracionViewModel

@Composable
fun NuevaActividadScreen(
    viewModel: AdministracionViewModel
) {

    // DESTINO DE GUARDADO (Servidor AWS vs Local / Room)
    var destinoGuardado by rememberSaveable {
        mutableStateOf("Servidor AWS")
    }

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
            text = "Completa los datos pedagógicos de la actividad y selecciona dónde deseas guardarla.",
            style = MaterialTheme.typography.bodyMedium
        )

        // SELECCIÓN DE DESTINO
        Text(
            text = "Destino de guardado",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = destinoGuardado == "Servidor AWS",
                onClick = {
                    destinoGuardado = "Servidor AWS"
                },
                label = {
                    Text("Servidor AWS (Asignación real)")
                }
            )

            FilterChip(
                selected = destinoGuardado == "Local / Room",
                onClick = {
                    destinoGuardado = "Local / Room"
                },
                label = {
                    Text("Local / Room (Pruebas)")
                }
            )
        }

        // MENSAJE INFORMATIVO DEL DESTINO
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (destinoGuardado == "Servidor AWS") {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                text = if (destinoGuardado == "Servidor AWS") {
                    "FLUJO PRINCIPAL: La actividad se guardará en PostgreSQL mediante Spring Boot (AWS). " +
                            "Estará disponible en el catálogo Servidor AWS de Asistente para asignaciones reales a estudiantes."
                } else {
                    "MODO LOCAL: La actividad se guardará únicamente en la base de datos local de este dispositivo (Room). " +
                            "No se enviará al Servidor AWS."
                },
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(12.dp)
            )
        }

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

        // OBJETIVO DE APRENDIZAJE
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

        // INSTRUCCIONES
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

        val guardandoActual = viewModel.guardando || viewModel.cargandoRemotas

        // GUARDAR ACTIVIDAD SEGÚN EL DESTINO SELECCIONADO
        Button(
            onClick = {

                if (destinoGuardado == "Servidor AWS") {

                    viewModel.crearActividadRemota(
                        titulo = titulo,
                        descripcion = descripcion,
                        modulo = modulo,
                        curso = curso,
                        dificultad = dificultad,
                        objetivo = objetivo,
                        instrucciones = instrucciones
                    )

                } else {

                    viewModel.guardarActividad(
                        titulo = titulo,
                        descripcion = descripcion,
                        modulo = modulo,
                        curso = curso,
                        dificultad = dificultad,
                        objetivo = objetivo,
                        instrucciones = instrucciones
                    )
                }
            },

            enabled =
                titulo.isNotBlank() &&
                        descripcion.isNotBlank() &&
                        objetivo.isNotBlank() &&
                        instrucciones.isNotBlank() &&
                        !guardandoActual,

            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {

            Text(
                when {
                    guardandoActual -> "Guardando..."
                    destinoGuardado == "Servidor AWS" -> "Guardar en Servidor AWS (PostgreSQL)"
                    else -> "Guardar en Local / Room"
                }
            )
        }

        // MENSAJES FEEDBACK EXPLÍCITOS SEGÚN DESTINO
        if (destinoGuardado == "Servidor AWS" && viewModel.mensajeRemoto.isNotBlank()) {

            Text(
                text = viewModel.mensajeRemoto,
                style = MaterialTheme.typography.bodyMedium,
                color = if (viewModel.mensajeRemoto.contains("guardada", ignoreCase = true) ||
                    viewModel.mensajeRemoto.contains("PostgreSQL", ignoreCase = true)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )

        } else if (destinoGuardado == "Local / Room" && viewModel.mensaje.isNotBlank()) {

            Text(
                text = "[Local / Room] ${viewModel.mensaje}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
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

