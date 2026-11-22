package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.repository.administracion.ActividadRepository
import com.example.mpazneb.ui.viewmodel.AdministracionViewModel

@Composable
fun NuevaActividadConRoomScreen() {

    // ====================================
    // CONTEXTO DE LA APLICACIÓN
    // ====================================

    val contexto = LocalContext.current.applicationContext

    // ====================================
    // REPOSITORIO LOCAL ROOM
    // ====================================

    val repositorio = remember(contexto) {

        val actividadDatabase =
            ActividadDatabase.obtenerInstancia(contexto)

        val asignacionDatabase =
            AsignacionDatabase.obtenerInstancia(contexto)

        ActividadRepository(
            actividadDao = actividadDatabase.actividadDao(),
            asignacionDao = asignacionDatabase.asignacionDao()
        )
    }

    // ====================================
    // VIEWMODEL
    // ====================================

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

    // ====================================
    // NAVEGACIÓN
    // ====================================

    var pantalla by rememberSaveable {
        mutableStateOf("Nueva")
    }

    var actividadEnEdicion by remember {
        mutableStateOf<Actividad?>(null)
    }

    var actividadRemotaEnEdicion by remember {
        mutableStateOf<Actividad?>(null)
    }

    var actividadRemotaAEliminar by remember {
        mutableStateOf<Actividad?>(null)
    }

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    // ====================================
    // MENSAJES DE EDICIÓN
    // ====================================

    LaunchedEffect(viewModel.mensaje) {

        val mensajeActual = viewModel.mensaje

        if (
            mensajeActual == "Actividad editada correctamente." ||
            mensajeActual.startsWith("No se pudo editar la actividad.") ||
            mensajeActual == "No se encontró la actividad para editar."
        ) {

            snackbarHostState.showSnackbar(mensajeActual)
        }
    }

    // ====================================
    // CARGAR ACTIVIDADES
    // ====================================

    LaunchedEffect(pantalla) {

        if (
            pantalla == "Matemática" ||
            pantalla == "Lectura" ||
            pantalla == "Preguntas" ||
            pantalla == "Resultados"
        ) {

            viewModel.cargarActividades()
        }

        if (pantalla == "Servidor" || pantalla == "Preguntas servidor") {

            viewModel.cargarActividadesRemotas()
        }
    }

    // ====================================
    // FORMULARIO REMOTO
    // ====================================

    var tituloRemoto by rememberSaveable {
        mutableStateOf("")
    }

    var descripcionRemota by rememberSaveable {
        mutableStateOf("")
    }

    var objetivoRemoto by rememberSaveable {
        mutableStateOf("")
    }

    var instruccionesRemotas by rememberSaveable {
        mutableStateOf("")
    }

    var moduloRemoto by rememberSaveable {
        mutableStateOf("Lectura")
    }

    var cursoRemoto by rememberSaveable {
        mutableIntStateOf(1)
    }

    var dificultadRemota by rememberSaveable {
        mutableStateOf("Básica")
    }

    // ====================================
    // INTERFAZ PRINCIPAL
    // ====================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ====================================
            // MENÚ ADMINISTRACIÓN
            // ====================================

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
                    "Resultados",
                    "Servidor",
                    "Preguntas servidor",
                    "Resultados servidor",
                    "Estudiantes servidor"
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

            // ====================================
            // CONTENIDO
            // ====================================

            Box(
                modifier = Modifier.weight(1f)
            ) {

                when (pantalla) {

                    // ====================================
                    // NUEVA ACTIVIDAD LOCAL
                    // ====================================

                    "Nueva" -> {

                        NuevaActividadScreen(
                            viewModel = viewModel
                        )
                    }

                    // ====================================
                    // MATEMÁTICA
                    // ====================================

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

                    // ====================================
                    // LECTURA
                    // ====================================

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

                    // ====================================
                    // PREGUNTAS
                    // ====================================

                    "Preguntas" -> {

                        GestionPreguntasScreen(
                            actividades = viewModel.actividades
                        )
                    }

                    // ====================================
                    // USUARIOS
                    // ====================================

                    "Usuarios" -> {

                        AdministrarUsuariosScreen()
                    }

                    // ====================================
                    // RESULTADOS
                    // ====================================

                    "Resultados" -> {

                        ResultadosGeneralesScreen(
                            actividades = viewModel.actividades
                        )
                    }

                    // ====================================
                    // SERVIDOR SPRING BOOT
                    // ====================================


                    "Estudiantes servidor" -> {

                        RegistroEstudiantesScreen()
                    }

                    "Resultados servidor" -> {

                        ResultadosRemotosScreen()
                    }

                    "Preguntas servidor" -> {

                        if (viewModel.cargandoRemotas || viewModel.guardando) {

                            CircularProgressIndicator()

                        } else if (viewModel.actividadesRemotas.isEmpty()) {

                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Text("No hay actividades remotas disponibles.")

                                Text(viewModel.mensajeRemoto)

                                Button(
                                    onClick = {
                                        viewModel.cargarActividadesRemotas()
                                    }
                                ) {
                                    Text("Consultar servidor")
                                }
                            }

                        } else {

                            GestionPreguntasScreen(
                                actividades = viewModel.actividadesRemotas,
                                modoRemoto = true
                            )
                        }
                    }

                    "Servidor" -> {

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {

                            Text(
                                text = "Administración del servidor",
                                style = MaterialTheme.typography.headlineSmall
                            )

                            Text(
                                text = "Actividades almacenadas en PostgreSQL mediante Spring Boot.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            HorizontalDivider()

                            // ====================================
                            // CREAR ACTIVIDAD REMOTA
                            // ====================================

                            Text(
                                text = "Crear actividad en PostgreSQL",
                                style = MaterialTheme.typography.titleLarge
                            )

                            // TÍTULO
                            OutlinedTextField(
                                value = tituloRemoto,

                                onValueChange = {
                                    tituloRemoto = it
                                },

                                label = {
                                    Text("Título de la actividad")
                                },

                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            // DESCRIPCIÓN
                            OutlinedTextField(
                                value = descripcionRemota,

                                onValueChange = {
                                    descripcionRemota = it
                                },

                                label = {
                                    Text("Descripción")
                                },

                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            // OBJETIVO
                            OutlinedTextField(
                                value = objetivoRemoto,

                                onValueChange = {
                                    objetivoRemoto = it
                                },

                                label = {
                                    Text("Objetivo de aprendizaje")
                                },

                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            // INSTRUCCIONES
                            OutlinedTextField(
                                value = instruccionesRemotas,

                                onValueChange = {
                                    instruccionesRemotas = it
                                },

                                label = {
                                    Text("Instrucciones para el estudiante")
                                },

                                modifier = Modifier.fillMaxWidth(),
                                minLines = 2
                            )

                            // ====================================
                            // SELECCIONAR MÓDULO
                            // ====================================

                            Text(
                                text = "Módulo",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                FilterChip(
                                    selected = moduloRemoto == "Lectura",

                                    onClick = {
                                        moduloRemoto = "Lectura"
                                    },

                                    label = {
                                        Text("Lectura")
                                    }
                                )

                                FilterChip(
                                    selected = moduloRemoto == "Matemática",

                                    onClick = {
                                        moduloRemoto = "Matemática"
                                    },

                                    label = {
                                        Text("Matemática")
                                    }
                                )
                            }

                            // ====================================
                            // SELECCIONAR CURSO
                            // ====================================

                            Text(
                                text = "Curso",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.horizontalScroll(
                                    rememberScrollState()
                                ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                (1..4).forEach { numero ->

                                    FilterChip(
                                        selected = cursoRemoto == numero,

                                        onClick = {
                                            cursoRemoto = numero
                                        },

                                        label = {
                                            Text("${numero}°")
                                        }
                                    )
                                }
                            }

                            // ====================================
                            // SELECCIONAR DIFICULTAD
                            // ====================================

                            Text(
                                text = "Dificultad",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.horizontalScroll(
                                    rememberScrollState()
                                ),

                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                listOf(
                                    "Básica",
                                    "Media",
                                    "Avanzada"
                                ).forEach { opcion ->

                                    FilterChip(
                                        selected = dificultadRemota == opcion,

                                        onClick = {
                                            dificultadRemota = opcion
                                        },

                                        label = {
                                            Text(opcion)
                                        }
                                    )
                                }
                            }

                            // ====================================
                            // GUARDAR EN SPRING BOOT
                            // ====================================

                            Button(
                                onClick = {

                                    viewModel.crearActividadRemota(
                                        titulo = tituloRemoto,
                                        descripcion = descripcionRemota,
                                        modulo = moduloRemoto,
                                        curso = cursoRemoto,
                                        dificultad = dificultadRemota,
                                        objetivo = objetivoRemoto,
                                        instrucciones = instruccionesRemotas
                                    )
                                },

                                enabled =
                                    tituloRemoto.isNotBlank() &&
                                            descripcionRemota.isNotBlank() &&
                                            objetivoRemoto.isNotBlank() &&
                                            instruccionesRemotas.isNotBlank() &&
                                            !viewModel.guardando && !viewModel.cargandoRemotas,

                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {

                                Text(
                                    if (viewModel.guardando) {
                                        "Guardando en PostgreSQL..."
                                    } else {
                                        "Guardar en servidor"
                                    }
                                )
                            }

                            // ====================================
                            // MENSAJES DEL SERVIDOR
                            // ====================================

                            if (viewModel.mensajeRemoto.isNotBlank()) {

                                Text(
                                    text = viewModel.mensajeRemoto,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            HorizontalDivider()

                            // ====================================
                            // LISTADO REMOTO
                            // ====================================

                            Text(
                                text = "Actividades registradas",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Button(
                                onClick = {
                                    viewModel.cargarActividadesRemotas()
                                },

                                enabled = !viewModel.cargandoRemotas && !viewModel.guardando
                            ) {

                                Text(
                                    if (viewModel.cargandoRemotas) {
                                        "Consultando..."
                                    } else {
                                        "Actualizar actividades"
                                    }
                                )
                            }

                            // ====================================
                            // MOSTRAR ACTIVIDADES REMOTAS
                            // ====================================

                            viewModel.actividadesRemotas.forEach { actividad ->

                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {

                                    Column(
                                        modifier = Modifier.padding(16.dp),

                                        verticalArrangement =
                                            Arrangement.spacedBy(6.dp)
                                    ) {

                                        Text(
                                            text = actividad.titulo,
                                            style = MaterialTheme.typography.titleMedium
                                        )

                                        Text(
                                            text = "ID: ${actividad.id}"
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
                                            text = actividad.descripcion
                                        )


                                        Text(
                                            text = if (actividad.activa) {
                                                "Estado: Activa"
                                            } else {
                                                "Estado: Inactiva"
                                            },
                                            color = if (actividad.activa) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.error
                                            }
                                        )

                                        Text(
                                            text = "Objetivo: ${actividad.objetivo}"
                                        )

                                        Text(
                                            text = "Instrucciones: ${actividad.instrucciones}"
                                        )

                                        HorizontalDivider()

// EDITAR ACTIVIDAD REMOTA

                                        OutlinedButton(
                                            onClick = {
                                                actividadRemotaEnEdicion = actividad
                                            },
                                            enabled = !viewModel.guardando &&
                                                    !viewModel.cargandoRemotas,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Editar actividad")
                                        }

// ACTIVAR O DESACTIVAR ACTIVIDAD REMOTA

                                        Button(
                                            onClick = {
                                                viewModel.cambiarEstadoActividadRemota(
                                                    actividad
                                                )
                                            },
                                            enabled = !viewModel.guardando &&
                                                    !viewModel.cargandoRemotas,
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
                                        // ELIMINAR ACTIVIDAD REMOTA

                                        OutlinedButton(
                                            onClick = {
                                                actividadRemotaAEliminar = actividad
                                            },
                                            enabled = !actividad.activa &&
                                                    !viewModel.guardando &&
                                                    !viewModel.cargandoRemotas,
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = MaterialTheme.colorScheme.error
                                            )
                                        ) {
                                            Text("Eliminar actividad")
                                        }


                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ====================================
        // MENSAJES DE ROOM
        // ====================================

        SnackbarHost(
            hostState = snackbarHostState,

            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }

    // ====================================
    // VENTANA DE EDICIÓN LOCAL
    // ====================================

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

    // ====================================
    // VENTANA DE EDICIÓN REMOTA
    // ====================================

    actividadRemotaEnEdicion?.let { actividad ->

        EditarActividadDialog(
            actividad = actividad,
            guardando = viewModel.guardando,

            onDismiss = {
                actividadRemotaEnEdicion = null
            },

            onGuardar = {
                    titulo,
                    descripcion,
                    curso,
                    dificultad,
                    objetivo,
                    instrucciones ->

                viewModel.editarActividadRemota(
                    actividad = actividad,
                    titulo = titulo,
                    descripcion = descripcion,
                    curso = curso,
                    dificultad = dificultad,
                    objetivo = objetivo,
                    instrucciones = instrucciones
                )

                actividadRemotaEnEdicion = null
            }
        )
    }



    // ====================================
    // CONFIRMAR ELIMINACION REMOTA
    // ====================================

    actividadRemotaAEliminar?.let { actividad ->

        AlertDialog(
            onDismissRequest = {
                actividadRemotaAEliminar = null
            },

            title = {
                Text("Eliminar actividad")
            },

            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Deseas eliminar la actividad '${actividad.titulo}'?"
                    )

                    Text(
                        "Esta accion es permanente."
                    )

                    Text(
                        "El servidor verificara que la actividad este inactiva y no tenga preguntas, asignaciones ni respuestas asociadas."
                    )
                }
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        viewModel.eliminarActividadRemota(
                            actividad
                        )

                        actividadRemotaAEliminar = null
                    },

                    enabled = !viewModel.guardando &&
                            !viewModel.cargandoRemotas,

                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        actividadRemotaAEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}