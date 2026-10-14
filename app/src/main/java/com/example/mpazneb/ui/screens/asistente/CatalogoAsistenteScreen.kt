
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun CatalogoAsistenteScreen(
    actividades: List<ActividadAula>,
    onIrAsignar: () -> Unit
) {

    // FILTROS SELECCIONADOS
    var moduloSeleccionado by rememberSaveable {
        mutableStateOf("Todos")
    }

    var cursoSeleccionado by rememberSaveable {
        mutableIntStateOf(0)
    }

    // ACTIVIDADES QUE CUMPLEN LOS FILTROS
    val actividadesFiltradas = actividades.filter { actividad ->

        val moduloCoincide =
            moduloSeleccionado == "Todos" ||
                    actividad.modulo == moduloSeleccionado

        val cursoCoincide =
            cursoSeleccionado == 0 ||
                    actividad.curso == cursoSeleccionado

        actividad.estado == "Disponible" &&
                moduloCoincide &&
                cursoCoincide
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // TITULO PRINCIPAL
        TituloAsistente(
            titulo = "Catálogo de actividades",
            descripcion = "Explora actividades de Lectura y Matemática."
        )

        Spacer(modifier = Modifier.height(20.dp))

        // FILTRO POR MODULO
        Text(
            text = "Filtrar por módulo",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Todos",
                "Lectura",
                "Matemática"
            ).forEach { modulo ->

                FilterChip(
                    selected = moduloSeleccionado == modulo,
                    onClick = {
                        moduloSeleccionado = modulo
                    },
                    label = {
                        Text(modulo)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FILTRO POR CURSO
        Text(
            text = "Filtrar por curso",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                            if (curso == 0) "Todos"
                            else "${curso}° básico"
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Actividades encontradas: ${actividadesFiltradas.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        // LISTADO DE ACTIVIDADES
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (actividadesFiltradas.isEmpty()) {

                item {
                    Text(
                        text = "No hay actividades disponibles para los filtros seleccionados.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                items(
                    items = actividadesFiltradas,
                    key = { it.id }
                ) { actividad ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = actividad.titulo,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Módulo: ${actividad.modulo}"
                            )

                            Text(
                                text = "Curso: ${actividad.curso}° básico"
                            )

                            Text(
                                text = "Dificultad: ${actividad.dificultad}"
                            )

                            Text(
                                text = actividad.descripcion,
                                style =
                                    MaterialTheme.typography.bodyMedium
                            )

                            BotonAsistente(
                                texto = "Ir a asignar",
                                onClick = onIrAsignar
                            )
                        }
                    }
                }
            }
        }
    }
}

// VISTA PREVIA PARA ANDROID STUDIO
@Preview(showBackground = true)
@Composable
fun CatalogoAsistentePreview() {

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
            descripcion = "Sumas de números naturales.",
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

    MPAZNEBTheme {
        CatalogoAsistenteScreen(
            actividades = actividadesDemo,
            onIrAsignar = {}
        )
    }
}
