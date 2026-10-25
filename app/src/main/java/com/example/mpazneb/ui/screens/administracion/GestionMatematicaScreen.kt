
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
fun GestionMatematicaScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier,
    onCambiarEstado: (Actividad) -> Unit = {},
    onEditar: (Actividad) -> Unit = {}
) {

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var cursoSeleccionado by rememberSaveable {
        mutableIntStateOf(0)
    }

    // FILTRAR ACTIVIDADES DE MATEMÁTICA
    val actividadesMatematica = actividades.filter { actividad ->
        actividad.modulo == "Matemática" &&
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
            text = "Gestionar Matemática",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Consulta, activa y edita las actividades de Matemática.",
            style = MaterialTheme.typography.bodyMedium
        )

        // BUSCADOR
        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text("Buscar actividad de matemática")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // FILTROS POR CURSO
        Text(
            text = "Filtrar por curso",
            style = MaterialTheme.typography.titleMedium
        )

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
            text = "Actividades encontradas: ${actividadesMatematica.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        // LISTA DE ACTIVIDADES
        if (actividadesMatematica.isEmpty()) {

            Text(
                text = "No se encontraron actividades de Matemática."
            )

        } else {

            actividadesMatematica.forEach { actividad ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        // TÍTULO
                        Text(
                            text = actividad.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        // DESCRIPCIÓN
                        Text(
                            text = actividad.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // NUEVO: OBJETIVO DE APRENDIZAJE
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

                        // NUEVO: INSTRUCCIONES
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

                        // CURSO
                        Text(
                            text = "${actividad.curso}° básico"
                        )

                        // DIFICULTAD
                        Text(
                            text = "Dificultad: ${actividad.dificultad}"
                        )

                        // ESTADO DE LA ACTIVIDAD
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

                        // ACTIVAR O DESACTIVAR
                        OutlinedButton(
                            onClick = {
                                onCambiarEstado(actividad)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                if (actividad.activa) {
                                    "Desactivar actividad"
                                } else {
                                    "Activar actividad"
                                }
                            )
                        }

                        // EDITAR ACTIVIDAD
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
fun GestionMatematicaPreview() {

    MPAZNEBTheme {

        GestionMatematicaScreen(
            actividades = listOf(

                Actividad(
                    id = 1,
                    titulo = "Pruebas de sumas Matías",
                    modulo = "Matemática",
                    curso = 2,
                    dificultad = "Media",
                    descripcion = "Actividad práctica de matemáticas.",
                    activa = false,
                    objetivo = "Resolver sumas de números naturales hasta 20.",
                    instrucciones = "Lee cada suma y selecciona la respuesta correcta."
                ),

                Actividad(
                    id = 2,
                    titulo = "Problemas cotidianos",
                    modulo = "Matemática",
                    curso = 3,
                    dificultad = "Básica",
                    descripcion = "Resolver problemas con sumas y restas.",
                    activa = false
                )
            )
        )
    }
}
