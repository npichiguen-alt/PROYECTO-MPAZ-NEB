
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun EstudiantesAsistenteScreen(
    estudiantes: List<EstudianteAula>,
    asignaciones: List<AsignacionAula>,
    onIrAsignar: () -> Unit
) {

    // TEXTO ESCRITO EN EL BUSCADOR
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    // FILTRO PARA MOSTRAR ESTUDIANTES ACTIVOS
    var soloActivos by rememberSaveable {
        mutableStateOf(true)
    }

    // FILTRAR LA LISTA DE ESTUDIANTES
    val estudiantesFiltrados = estudiantes.filter { estudiante ->

        val coincideBusqueda = estudiante.alias.contains(
            busqueda.trim(),
            ignoreCase = true
        )

        val coincideEstado = !soloActivos || estudiante.activo

        coincideBusqueda && coincideEstado
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // TITULO PRINCIPAL
        TituloAsistente(
            titulo = "Estudiantes",
            descripcion = "Consulta estudiantes ficticios y sus asignaciones."
        )

        // BUSCADOR
        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text("Buscar estudiante")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // FILTRO POR ESTADO
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = soloActivos,
                onCheckedChange = {
                    soloActivos = it
                }
            )

            Text("Mostrar solo estudiantes activos")
        }

        // CANTIDAD DE ESTUDIANTES
        Text(
            text = "Estudiantes encontrados: ${estudiantesFiltrados.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        // LISTA DE ESTUDIANTES
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (estudiantesFiltrados.isEmpty()) {

                item {
                    Text(
                        text = "No se encontraron estudiantes.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

            } else {

                items(
                    items = estudiantesFiltrados,
                    key = { it.alias }
                ) { estudiante ->

                    // CONTAR LAS ASIGNACIONES DEL ESTUDIANTE
                    val cantidadAsignaciones = asignaciones.count {
                        it.estudianteAlias == estudiante.alias
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = estudiante.alias,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (estudiante.activo) {
                                    "Estado: Activo"
                                } else {
                                    "Estado: Inactivo"
                                }
                            )

                            Text(
                                text = "Actividades asignadas: $cantidadAsignaciones"
                            )

                            // SOLO LOS ESTUDIANTES ACTIVOS
                            // PUEDEN RECIBIR NUEVAS ASIGNACIONES
                            if (estudiante.activo) {

                                BotonAsistente(
                                    texto = "Asignar actividad",
                                    onClick = onIrAsignar
                                )
                            }
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
fun EstudiantesAsistentePreview() {

    val estudiantesDemo = listOf(
        EstudianteAula("Estudiante A", true),
        EstudianteAula("Estudiante B", true),
        EstudianteAula("Estudiante C", true),
        EstudianteAula("Estudiante D", false)
    )

    val asignacionesDemo = listOf(
        AsignacionAula(
            id = 101,
            actividadId = 1,
            actividadTitulo = "Cuento del bosque",
            estudianteAlias = "Estudiante A",
            codigoTemporal = "DEMO-204",
            asistenteAlias = "ASISTENTE_DEMO",
            curso = 2,
            estado = "Completada"
        )
    )

    MPAZNEBTheme {
        EstudiantesAsistenteScreen(
            estudiantes = estudiantesDemo,
            asignaciones = asignacionesDemo,
            onIrAsignar = {}
        )
    }
}
