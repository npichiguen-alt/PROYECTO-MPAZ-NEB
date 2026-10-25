
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.repository.administracion.ActividadRepository
import com.example.mpazneb.data.local.AsignacionDatabase
@Composable
fun NuevaActividadConRoomScreen() {

    // CONTEXTO DE LA APLICACIÓN
    val contexto = LocalContext.current.applicationContext

    // REPOSITORIO CON ROOM
    val repositorio = remember(contexto) {

        // Actividades y preguntas de Administración.
        val actividadDatabase =
            ActividadDatabase.obtenerInstancia(contexto)

        // Asignaciones del módulo Asistente.
        val asignacionDatabase =
            AsignacionDatabase.obtenerInstancia(contexto)

        ActividadRepository(
            actividadDao =
                actividadDatabase.actividadDao(),

            asignacionDao =
                asignacionDatabase.asignacionDao()
        )
    }

    // FÁBRICA DEL VIEWMODEL
    val fabrica = remember(repositorio) {

        viewModelFactory {

            initializer {
                AdministracionViewModel(repositorio)
            }
        }
    }

    val viewModel: AdministracionViewModel = viewModel(
        factory = fabrica
    )

    // PANTALLA SELECCIONADA
    var pantalla by rememberSaveable {
        mutableStateOf("Nueva")
    }

    // ACTIVIDAD SELECCIONADA PARA EDITAR
    var actividadEnEdicion by remember {
        mutableStateOf<Actividad?>(null)
    }

    // MENSAJES DE EDICIÓN
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(viewModel.mensaje) {

        val mensajeActual = viewModel.mensaje

        if (
            mensajeActual == "Actividad editada correctamente." ||
            mensajeActual.startsWith(
                "No se pudo editar la actividad."
            ) ||
            mensajeActual == "No se encontró la actividad para editar."
        ) {
            snackbarHostState.showSnackbar(mensajeActual)
        }
    }

    // CARGAR ACTIVIDADES DESDE ROOM
    // NUEVO: TAMBIÉN AL ENTRAR A PREGUNTAS
    LaunchedEffect(pantalla) {

        if (
            pantalla == "Matemática" ||
            pantalla == "Lectura" ||
            pantalla == "Preguntas" ||
            pantalla == "Resultados"
        ) {
            viewModel.cargarActividades()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // MENÚ DE ADMINISTRACIÓN
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                listOf(
                    "Nueva",
                    "Matemática",
                    "Lectura",
                    "Preguntas",
                    "Usuarios",
                    "Resultados"
                ).forEach { opcion ->

                    FilterChip(
                        selected = pantalla == opcion,

                        onClick = {
                            pantalla = opcion
                        },

                        label = {
                            Text(opcion)
                        }
                    )
                }
            }

            // CONTENIDO DE LAS SECCIONES
            Box(
                modifier = Modifier.weight(1f)
            ) {

                when (pantalla) {

                    // NUEVA ACTIVIDAD
                    "Nueva" -> {

                        NuevaActividadScreen(
                            viewModel = viewModel
                        )
                    }

                    // MATEMÁTICA
                    "Matemática" -> {

                        GestionMatematicaScreen(
                            actividades = viewModel.actividades,

                            onCambiarEstado =
                                viewModel::cambiarEstadoActividad,

                            onEditar = { actividad ->
                                actividadEnEdicion = actividad
                            }
                        )
                    }

                    // LECTURA
                    "Lectura" -> {

                        GestionLecturaScreen(
                            actividades = viewModel.actividades,

                            onCambiarEstado =
                                viewModel::cambiarEstadoActividad,

                            onEditar = { actividad ->
                                actividadEnEdicion = actividad
                            }
                        )
                    }

                    // NUEVO: GESTIONAR PREGUNTAS
                    "Preguntas" -> {

                        GestionPreguntasScreen(
                            actividades = viewModel.actividades
                        )
                    }

                    // USUARIOS
                    "Usuarios" -> {

                        AdministrarUsuariosScreen()
                    }

                    // RESULTADOS
                    "Resultados" -> {

                        ResultadosGeneralesScreen(
                            actividades = viewModel.actividades
                        )
                    }
                }
            }
        }

        // MOSTRAR MENSAJES DE GUARDADO Y EDICIÓN
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }

    // VENTANA DE EDICIÓN DE ACTIVIDADES
    actividadEnEdicion?.let { actividad ->

        EditarActividadDialog(
            actividad = actividad,
            guardando = viewModel.guardando,

            onDismiss = {
                actividadEnEdicion = null
            },

            onGuardar = {
                    titulo,
                    descripcion,
                    curso,
                    dificultad,
                    objetivo,
                    instrucciones ->

                viewModel.editarActividad(
                    actividad = actividad,
                    titulo = titulo,
                    descripcion = descripcion,
                    curso = curso,
                    dificultad = dificultad,
                    objetivo = objetivo,
                    instrucciones = instrucciones
                )

                actividadEnEdicion = null
            }
        )
    }
}
