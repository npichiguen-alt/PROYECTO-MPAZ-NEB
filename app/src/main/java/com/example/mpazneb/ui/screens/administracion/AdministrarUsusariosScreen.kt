
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private data class UsuarioEjemplo(
    val nombre: String,
    val perfil: String,
    val activo: Boolean
)

@Composable
fun AdministrarUsuariosScreen() {

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var perfilSeleccionado by rememberSaveable {
        mutableStateOf("Todos")
    }

    val usuarios = remember {
        mutableStateListOf(
            UsuarioEjemplo("Estudiante de prueba 1", "Estudiante", true),
            UsuarioEjemplo("Estudiante de prueba 2", "Estudiante", false),
            UsuarioEjemplo("Asistente de prueba", "Asistente de aula", true)
        )
    }

    val usuariosFiltrados = usuarios.filter { usuario ->
        usuario.nombre.contains(busqueda.trim(), ignoreCase = true) &&
                (perfilSeleccionado == "Todos" ||
                        usuario.perfil == perfilSeleccionado)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Administrar usuarios",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar usuario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Text(
            text = "Filtrar por perfil",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Todos", "Estudiante", "Asistente de aula").forEach { perfil ->
                FilterChip(
                    selected = perfilSeleccionado == perfil,
                    onClick = { perfilSeleccionado = perfil },
                    label = { Text(perfil) }
                )
            }
        }

        Text("Usuarios encontrados: ${usuariosFiltrados.size}")

        if (usuariosFiltrados.isEmpty()) {
            Text("No se encontraron usuarios.")
        } else {
            usuariosFiltrados.forEach { usuario ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = usuario.nombre,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("Perfil: ${usuario.perfil}")

                        Text(
                            if (usuario.activo) {
                                "Estado: Activo"
                            } else {
                                "Estado: Inactivo"
                            }
                        )

                        OutlinedButton(
                            onClick = {
                                val posicion = usuarios.indexOf(usuario)

                                if (posicion >= 0) {
                                    usuarios[posicion] = usuario.copy(
                                        activo = !usuario.activo
                                    )
                                }
                            }
                        ) {
                            Text(
                                if (usuario.activo) "Desactivar" else "Activar"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdministrarUsuariosPreview() {
    MaterialTheme {
        AdministrarUsuariosScreen()
    }
}
