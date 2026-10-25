
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.mpazneb.data.local.UsuarioDatabase
import com.example.mpazneb.data.local.UsuarioEntity

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
fun AdministrarUsuariosScreen() {

    // ====================================
    // 1. CONEXIÓN CON ROOM
    // ====================================

    val contexto =
        LocalContext.current.applicationContext

    val usuarioDao = remember(contexto) {
        UsuarioDatabase
            .obtenerInstancia(contexto)
            .usuarioDao()
    }

    val flujoUsuarios = remember(usuarioDao) {
        usuarioDao.observarUsuarios()
    }

    val usuarios by flujoUsuarios.collectAsState(
        initial = emptyList()
    )

    val scope = rememberCoroutineScope()

    // ====================================
    // 2. FORMULARIO DE USUARIOS
    // ====================================

    var aliasNuevo by rememberSaveable {
        mutableStateOf("")
    }

    var perfilNuevo by rememberSaveable {
        mutableStateOf("Estudiante")
    }

    var guardando by remember {
        mutableStateOf(false)
    }

    var mensaje by remember {
        mutableStateOf("")
    }

    // ====================================
    // 3. FILTROS
    // ====================================

    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    var perfilSeleccionado by rememberSaveable {
        mutableStateOf("Todos")
    }

    var soloActivos by rememberSaveable {
        mutableStateOf(false)
    }

    val usuariosFiltrados = usuarios.filter { usuario ->

        val coincideBusqueda =
            usuario.alias.contains(
                busqueda.trim(),
                ignoreCase = true
            )

        val coincidePerfil =
            perfilSeleccionado == "Todos" ||
                    usuario.perfil == perfilSeleccionado

        val coincideEstado =
            !soloActivos || usuario.activo

        coincideBusqueda &&
                coincidePerfil &&
                coincideEstado
    }

    // ====================================
    // 4. PANTALLA
    // ====================================

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
                text = "Administrar usuarios",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {

            Text(
                text = "Registra y administra " +
                        "estudiantes y asistentes ficticios.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ====================================
        // CREAR USUARIO
        // ====================================

        item {

            Text(
                text = "Registrar nuevo usuario",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            OutlinedTextField(
                value = aliasNuevo,

                onValueChange = {
                    aliasNuevo = it
                    mensaje = ""
                },

                label = {
                    Text("Alias del usuario")
                },

                placeholder = {
                    Text("Ej.: Estudiante A")
                },

                singleLine = true,
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            Text(
                text = "Perfil",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "Estudiante",
                    "Asistente de aula"
                ).forEach { perfil ->

                    FilterChip(
                        selected = perfilNuevo == perfil,

                        onClick = {
                            perfilNuevo = perfil
                        },

                        enabled = !guardando,

                        label = {
                            Text(perfil)
                        }
                    )
                }
            }
        }

        item {

            Button(
                onClick = {

                    val aliasLimpio =
                        aliasNuevo
                            .trim()
                            .replace(
                                Regex("\\s+"),
                                " "
                            )

                    when {

                        aliasLimpio.length !in 2..60 -> {

                            mensaje =
                                "El alias debe tener entre " +
                                        "2 y 60 caracteres."
                        }

                        usuarios.any {
                            it.alias.equals(
                                aliasLimpio,
                                ignoreCase = true
                            )
                        } -> {

                            mensaje =
                                "Ya existe un usuario " +
                                        "con ese alias."
                        }

                        else -> {

                            guardando = true

                            scope.launch {

                                try {

                                    usuarioDao.insertarUsuario(
                                        UsuarioEntity(
                                            alias = aliasLimpio,
                                            perfil = perfilNuevo,
                                            activo = true
                                        )
                                    )

                                    aliasNuevo = ""

                                    mensaje =
                                        "Usuario registrado correctamente."

                                } catch (
                                    error: CancellationException
                                ) {

                                    throw error

                                } catch (error: Exception) {

                                    mensaje =
                                        "No se pudo registrar el usuario. " +
                                                "Comprueba que el alias sea único."

                                } finally {

                                    guardando = false
                                }
                            }
                        }
                    }
                },

                enabled =
                    !guardando &&
                            aliasNuevo.isNotBlank(),

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    if (guardando) {
                        "Guardando..."
                    } else {
                        "Registrar usuario"
                    }
                )
            }
        }

        if (mensaje.isNotBlank()) {

            item {

                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        item {
            HorizontalDivider()
        }

        // ====================================
        // BUSCAR Y FILTRAR
        // ====================================

        item {

            Text(
                text = "Usuarios registrados",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            OutlinedTextField(
                value = busqueda,

                onValueChange = {
                    busqueda = it
                },

                label = {
                    Text("Buscar usuario")
                },

                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {

            Row(
                modifier = Modifier.horizontalScroll(
                    rememberScrollState()
                ),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "Todos",
                    "Estudiante",
                    "Asistente de aula"
                ).forEach { perfil ->

                    FilterChip(
                        selected =
                            perfilSeleccionado == perfil,

                        onClick = {
                            perfilSeleccionado = perfil
                        },

                        label = {
                            Text(perfil)
                        }
                    )
                }
            }
        }

        item {

            Row {

                Checkbox(
                    checked = soloActivos,

                    onCheckedChange = {
                        soloActivos = it
                    }
                )

                Text(
                    text = "Mostrar solo usuarios activos",
                    modifier = Modifier.padding(
                        top = 12.dp
                    )
                )
            }
        }

        item {

            Text(
                text = "Total registrados: ${usuarios.size}"
            )

            Text(
                text = "Usuarios encontrados: " +
                        usuariosFiltrados.size,
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ====================================
        // LISTADO DE USUARIOS
        // ====================================

        if (usuariosFiltrados.isEmpty()) {

            item {

                Text(
                    text = if (usuarios.isEmpty()) {
                        "Todavía no hay usuarios registrados."
                    } else {
                        "No hay usuarios con esos filtros."
                    }
                )
            }

        } else {

            items(
                items = usuariosFiltrados,
                key = { it.id }
            ) { usuario ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = usuario.alias,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Perfil: ${usuario.perfil}"
                        )

                        Text(
                            text = if (usuario.activo) {
                                "Estado: Activo"
                            } else {
                                "Estado: Inactivo"
                            }
                        )

                        OutlinedButton(
                            onClick = {

                                guardando = true

                                scope.launch {

                                    try {

                                        val actualizado =
                                            usuarioDao.actualizarEstado(
                                                id = usuario.id,
                                                activo = !usuario.activo
                                            )

                                        mensaje = if (
                                            actualizado > 0
                                        ) {
                                            "Estado actualizado correctamente."
                                        } else {
                                            "No se encontró el usuario."
                                        }

                                    } catch (
                                        error: CancellationException
                                    ) {

                                        throw error

                                    } catch (error: Exception) {

                                        mensaje =
                                            "No se pudo actualizar el usuario."

                                    } finally {

                                        guardando = false
                                    }
                                }
                            },

                            enabled = !guardando,

                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                if (usuario.activo) {
                                    "Desactivar usuario"
                                } else {
                                    "Activar usuario"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
