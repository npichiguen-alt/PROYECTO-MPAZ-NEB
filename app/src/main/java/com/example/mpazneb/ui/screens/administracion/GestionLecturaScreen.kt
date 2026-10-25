
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

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun GestionLecturaScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier,
    onCambiarEstado: (Actividad) -> Unit = {},
    onEditar: (Actividad) -> Unit = {}
) {

    // Guardar la búsqueda del usuario
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    // Guardar el curso seleccionado
    var cursoSeleccionado by rememberSaveable {
        mutableIntStateOf(0)
    }

    // Filtrar las actividades de Lectura
    val actividadesLectura = actividades.filter { actividad ->
        actividad.modulo == "Lectura" &&
                actividad.titulo.contains(
                    busqueda.trim(),
                    ignoreCase = true
                ) &&
                (
                        cursoSeleccionado == 0 ||
                                actividad.curso == cursoSeleccionado
                        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Gestionar Lectura",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Consulta y administra las actividades de Lectura.",
            style = MaterialTheme.typography.bodyMedium
        )

        // Buscador
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = {
                Text("Buscar actividad de lectura")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Filtrar por curso",
            style = MaterialTheme.typography.titleMedium
        )

        // Filtros de curso
        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            (0..4).forEach { curso ->

                FilterChip(
                    selected = cursoSeleccionado == curso,
                    onClick = {
                        cursoSeleccionado = curso
                    },
                    label = {
                        Text(
                            if (curso == 0) {
                                "Todos"
                            } else {
                                "$curso.º básico"
                            }
                        )
                    }
                )
            }
        }

        Text(
            text = "Actividades encontradas: ${actividadesLectura.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        // Mostrar actividades
        if (actividadesLectura.isEmpty()) {

            Text(
                text = "No se encontraron actividades de Lectura."
            )

        } else {

            actividadesLectura.forEach { actividad ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // Nombre de la actividad
                        Text(
                            text = actividad.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Descripción
                        Text(
                            text = actividad.descripcion
                        )

                        if (actividad.objetivo.isNotBlank()) {

                            HorizontalDivider()

                            Text(
                                text = "Objetivo de aprendizaje",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = actividad.objetivo,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        if (actividad.instrucciones.isNotBlank()) {

                            Text(
                                text = "Instrucciones para el estudiante",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = actividad.instrucciones,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }


                        // Curso
                        Text(
                            text = "${actividad.curso}° básico"
                        )

                        // Dificultad
                        Text(
                            text = "Dificultad: ${actividad.dificultad}"
                        )

                        // Estado actual
                        Text(
                            text = if (actividad.activa) {
                                "Estado: Activa"
                            } else {
                                "Estado: Borrador"
                            },
                            color = if (actividad.activa) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        // NUEVO: ACTIVAR O DESACTIVAR
                        OutlinedButton(
                            onClick = {
                                onCambiarEstado(actividad)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = if (actividad.activa) {
                                    "Desactivar actividad"
                                } else {
                                    "Activar actividad"
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onEditar(actividad)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Editar actividad")
                        }

                    }
                }
            }
        }
    }
}

// VISTA PREVIA
@Preview(showBackground = true)
@Composable
fun GestionLecturaPreview() {

    MPAZNEBTheme {

        GestionLecturaScreen(
            actividades = listOf(

                Actividad(
                    id = 1,
                    titulo = "Cuento del bosque",
                    modulo = "Lectura",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Comprensión de un cuento corto.",
                    activa = true
                ),

                Actividad(
                    id = 2,
                    titulo = "Vocabulario divertido",
                    modulo = "Lectura",
                    curso = 3,
                    dificultad = "Media",
                    descripcion = "Aprender nuevas palabras.",
                    activa = false
                ),

                Actividad(
                    id = 3,
                    titulo = "Resolver sumas",
                    modulo = "Matemática",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Practicar sumas.",
                    activa = true
                )
            )
        )
    }
}
