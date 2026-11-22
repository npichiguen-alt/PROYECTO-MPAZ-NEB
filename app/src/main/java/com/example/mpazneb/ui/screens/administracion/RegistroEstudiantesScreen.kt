package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mpazneb.ui.viewmodel.RegistroEstudiantesViewModel

@Composable
fun RegistroEstudiantesScreen(
    viewModel: RegistroEstudiantesViewModel = viewModel()
) {

    var alias by rememberSaveable {
        mutableStateOf("")
    }

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    val ocupado = viewModel.cargando || viewModel.guardando

    val estudiantesFiltrados = viewModel.estudiantes.filter {

        it.alias.contains(
            busqueda.trim(),
            ignoreCase = true
        )
    }

    LaunchedEffect(viewModel) {

        viewModel.cargarEstudiantes()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp),

        contentPadding =
            PaddingValues(bottom = 24.dp)
    ) {

        item {

            Text(
                "Estudiantes del servidor",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                "Registro y consulta de estudiantes ficticios.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        "Registrar estudiante",
                        style = MaterialTheme.typography.titleLarge
                    )

                    OutlinedTextField(
                        value = alias,

                        onValueChange = {
                            alias = it
                        },

                        label = {
                            Text("Alias ficticio")
                        },

                        placeholder = {
                            Text("Estudiante-001")
                        },

                        singleLine = true,

                        enabled = !ocupado,

                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        "Utiliza solamente alias ficticios. No ingreses datos personales.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Button(
                        onClick = {

                            viewModel.registrarEstudiante(alias) {

                                alias = ""
                            }
                        },

                        enabled = !ocupado &&
                            alias.trim().length in 2..60,

                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            if (viewModel.guardando) {
                                "Registrando..."
                            } else {
                                "Registrar estudiante"
                            }
                        )
                    }
                }
            }
        }

        if (ocupado) {

            item {

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (viewModel.mensaje.isNotBlank()) {

            item {

                Text(
                    text = viewModel.mensaje,

                    color = if (viewModel.hayError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }

        item {

            HorizontalDivider()

            Spacer(Modifier.height(8.dp))

            Text(
                "Estudiantes registrados",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            OutlinedButton(
                onClick = {
                    viewModel.cargarEstudiantes()
                },

                enabled = !ocupado,

                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Actualizar estudiantes")
            }
        }

        if (viewModel.consultaExitosa) {

            item {

                Text(
                    "Total registrados: ${viewModel.estudiantes.size}"
                )

                Text(
                    "Activos: ${viewModel.estudiantes.count { it.activo }}"
                )
            }

        } else if (!ocupado) {

            item {

                Text(
                    "El listado todavia no esta confirmado por el servidor.",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        item {

            OutlinedTextField(
                value = busqueda,

                onValueChange = {
                    busqueda = it
                },

                label = {
                    Text("Buscar por alias")
                },

                singleLine = true,

                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            estudiantesFiltrados.isEmpty() &&
            !ocupado &&
            viewModel.consultaExitosa
        ) {

            item {

                Text(
                    if (busqueda.isBlank()) {
                        "No hay estudiantes registrados."
                    } else {
                        "No se encontraron coincidencias."
                    }
                )
            }
        }

        items(
            items = estudiantesFiltrados,
            key = { it.id }
        ) { estudiante ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        estudiante.alias,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("ID: ${estudiante.id}")

                    Text(
                        if (estudiante.activo) {
                            "Estado: Activo"
                        } else {
                            "Estado: Inactivo"
                        },

                        color = if (estudiante.activo) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        }
                    )
                }
            }
        }
    }
}