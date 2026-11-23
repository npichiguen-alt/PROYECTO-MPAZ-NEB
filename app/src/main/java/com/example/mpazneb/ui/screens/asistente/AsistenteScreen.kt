
package com.example.mpazneb.ui.screens.asistente

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mpazneb.repository.asistente.AsistenteDataSource
import com.example.mpazneb.repository.asistente.AsignacionRemotaRepository
import com.example.mpazneb.viewmodel.AsistenteViewModel
import com.example.mpazneb.viewmodel.AsistenteViewModelFactory

@Composable
fun AsistenteScreen(
    fuente: AsistenteDataSource
) {

    // CREAR EL VIEWMODEL DEL ASISTENTE

    val repositorioRemoto = remember {
        AsignacionRemotaRepository()
    }

    val vm: AsistenteViewModel = viewModel(
        factory = remember(fuente, repositorioRemoto) {
            AsistenteViewModelFactory(
                fuente = fuente,
                repositorioRemoto = repositorioRemoto
            )
        }
    )

    // DATOS LOCALES DE ROOM

    val actividades by vm.actividades.collectAsState()

    // DATOS REMOTOS DE AWS

    val estudiantes = vm.estudiantesRemotos
    val asignaciones = vm.asignacionesRemotas
    val resultados = vm.resultadosRemotos

    // PANTALLA ACTUAL

    var seccion by rememberSaveable {
        mutableStateOf("INICIO")
    }

    // ORIGEN DEL CATALOGO

    var origenCatalogo by rememberSaveable {
        mutableStateOf("AWS")
    }

    // CARGAR ACTIVIDADES DE AWS

    LaunchedEffect(seccion, origenCatalogo) {

        if (
            seccion == "INICIO" ||
            seccion == "ASIGNAR" ||
            (
                    seccion == "CATALOGO" &&
                            origenCatalogo == "AWS"
                    )
        ) {
            vm.cargarActividadesRemotas()
        }
    }

    // CARGAR ESTUDIANTES REMOTOS

    LaunchedEffect(seccion) {

        if (
            seccion == "ESTUDIANTES" ||
            seccion == "ASIGNAR"
        ) {
            vm.cargarEstudiantesRemotos()
        }
    }

    // ACTUALIZAR ASIGNACIONES Y RESULTADOS

    LaunchedEffect(seccion) {

        if (
            seccion == "INICIO" ||
            seccion == "ESTUDIANTES" ||
            seccion == "SEGUIMIENTO"
        ) {
            vm.cargarAsignacionesRemotas()
        }
    }

    // BOTON ATRAS DEL TELEFONO

    BackHandler(enabled = seccion != "INICIO") {
        seccion = "INICIO"
    }

    // OPCIONES DEL MENU INFERIOR

    val opciones = listOf(
        Triple("INICIO", "Inicio", Icons.Filled.Home),
        Triple("CATALOGO", "Catálogo", Icons.Filled.MenuBook),
        Triple("ESTUDIANTES", "Estud.", Icons.Filled.People),
        Triple("ASIGNAR", "Asignar", Icons.Filled.Assignment),
        Triple("SEGUIMIENTO", "Seguimiento", Icons.Filled.Assessment)
    )

    Scaffold(
        bottomBar = {

            NavigationBar {

                opciones.forEach { (clave, etiqueta, icono) ->

                    NavigationBarItem(
                        selected = seccion == clave,

                        onClick = {
                            seccion = clave
                            vm.limpiarMensaje()
                        },

                        icon = {
                            Icon(
                                imageVector = icono,
                                contentDescription = etiqueta
                            )
                        },

                        label = {
                            Text(etiqueta)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // MENSAJES DEL VIEWMODEL

            AnimatedVisibility(
                visible = vm.mensaje.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {

                        Text(
                            text = vm.mensaje,
                            modifier = Modifier.weight(1f)
                        )

                        TextButton(
                            onClick = {
                                vm.limpiarMensaje()
                            }
                        ) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            // CONTENIDO PRINCIPAL

            Box(
                modifier = Modifier.weight(1f)
            ) {

                when (seccion) {

                    // PANTALLA DE INICIO

                    "INICIO" -> {

                        AsistenteInicioScreen(
                            actividades = vm.actividadesRemotas,
                            asignaciones = asignaciones,

                            irCatalogo = {
                                seccion = "CATALOGO"
                            },

                            irAsignar = {
                                seccion = "ASIGNAR"
                            },

                            irResultados = {
                                seccion = "SEGUIMIENTO"
                            }
                        )
                    }

                    // CATALOGO DE ACTIVIDADES

                    "CATALOGO" -> {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // SELECCION DEL ORIGEN

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(8.dp)
                                ) {

                                    FilterChip(
                                        selected = origenCatalogo == "LOCAL",

                                        onClick = {
                                            origenCatalogo = "LOCAL"
                                        },

                                        label = {
                                            Text("Room local")
                                        }
                                    )

                                    FilterChip(
                                        selected = origenCatalogo == "AWS",

                                        onClick = {
                                            origenCatalogo = "AWS"
                                        },

                                        label = {
                                            Text("Servidor AWS")
                                        }
                                    )
                                }

                                if (origenCatalogo == "AWS") {
                                    TextButton(
                                        onClick = vm::cargarActividadesRemotas,
                                        enabled = !vm.cargandoActividadesRemotas
                                    ) {
                                        Text("Actualizar")
                                    }
                                }
                            }

                            // CARGANDO DATOS REMOTOS

                            if (
                                origenCatalogo == "AWS" &&
                                vm.cargandoActividadesRemotas
                            ) {

                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // ERRORES DE CONEXION

                            if (
                                origenCatalogo == "AWS" &&
                                vm.errorActividadesRemotas.isNotBlank()
                            ) {

                                Text(
                                    text = vm.errorActividadesRemotas,

                                    color =
                                        MaterialTheme.colorScheme.error,

                                    modifier = Modifier.padding(8.dp)
                                )

                                TextButton(
                                    onClick = vm::cargarActividadesRemotas
                                ) {
                                    Text("Reintentar")
                                }
                            }

                            // AVISO SOBRE EL MODO LOCAL

                            if (origenCatalogo == "LOCAL") {

                                Text(
                                    text = "Catálogo local de consulta. " +
                                            "Para asignar, selecciona Servidor AWS.",

                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 4.dp
                                    ),

                                    style =
                                        MaterialTheme.typography.bodySmall
                                )
                            }

                            // MOSTRAR CATALOGO

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                CatalogoAsistenteScreen(
                                    actividades =
                                        if (origenCatalogo == "AWS") {
                                            vm.actividadesRemotas
                                        } else {
                                            actividades
                                        },

                                    onIrAsignar = { actividadId ->

                                        if (
                                            origenCatalogo == "AWS"
                                        ) {

                                            vm.prepararActividad(
                                                actividadId
                                            )

                                            seccion = "ASIGNAR"
                                        }
                                    },

                                    permitirAsignar =
                                        origenCatalogo == "AWS"
                                )
                            }
                        }
                    }

                    // LISTADO DE ESTUDIANTES REMOTOS

                    "ESTUDIANTES" -> {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            Row(
                                modifier = Modifier.padding(8.dp)
                            ) {

                                Text(
                                    text = "Estudiantes del servidor AWS",

                                    modifier = Modifier.weight(1f),

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )

                                TextButton(
                                    onClick = {
                                        vm.cargarEstudiantesRemotos()
                                        vm.cargarAsignacionesRemotas()
                                    },

                                    enabled =
                                        !vm.cargandoEstudiantesRemotos &&
                                                !vm.trabajando
                                ) {
                                    Text("Actualizar")
                                }
                            }

                            // INDICADOR DE CARGA

                            if (vm.cargandoEstudiantesRemotos) {

                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // ERROR DE CONEXION

                            if (
                                vm.errorEstudiantesRemotos.isNotBlank()
                            ) {

                                Text(
                                    text = vm.errorEstudiantesRemotos,

                                    color =
                                        MaterialTheme.colorScheme.error,

                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            // LISTADO VACIO

                            if (
                                vm.estudiantesRemotosCargados &&
                                estudiantes.isEmpty()
                            ) {

                                Text(
                                    text = "No hay estudiantes activos. " +
                                            "Registra un estudiante desde Administración.",

                                    modifier = Modifier.padding(16.dp),

                                    style =
                                        MaterialTheme.typography.bodyMedium
                                )
                            }

                            // MOSTRAR ESTUDIANTES

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                EstudiantesAsistenteScreen(
                                    estudiantes = estudiantes,
                                    asignaciones = asignaciones,

                                    onIrAsignar = {
                                        seccion = "ASIGNAR"
                                    }
                                )
                            }
                        }
                    }

                    // ASIGNAR ACTIVIDADES DESDE AWS

                    "ASIGNAR" -> {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // INDICADOR DE CARGA

                            if (
                                vm.cargandoActividadesRemotas ||
                                vm.cargandoEstudiantesRemotos
                            ) {

                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            // ERROR DE ACTIVIDADES

                            if (
                                vm.errorActividadesRemotas.isNotBlank()
                            ) {

                                Text(
                                    text = vm.errorActividadesRemotas,

                                    color =
                                        MaterialTheme.colorScheme.error,

                                    modifier = Modifier.padding(8.dp)
                                )

                                TextButton(
                                    onClick =
                                        vm::cargarActividadesRemotas
                                ) {
                                    Text("Reintentar actividades")
                                }
                            }

                            // ERROR DE ESTUDIANTES

                            if (
                                vm.errorEstudiantesRemotos.isNotBlank()
                            ) {

                                Text(
                                    text = vm.errorEstudiantesRemotos,

                                    color =
                                        MaterialTheme.colorScheme.error,

                                    modifier = Modifier.padding(8.dp)
                                )

                                TextButton(
                                    onClick =
                                        vm::cargarEstudiantesRemotos
                                ) {
                                    Text("Reintentar estudiantes")
                                }
                            }

                            // AVISO SI NO HAY ESTUDIANTES

                            if (
                                vm.estudiantesRemotosCargados &&
                                estudiantes.isEmpty()
                            ) {

                                Text(
                                    text = "No hay estudiantes activos. " +
                                            "Debes registrarlos desde Administración.",

                                    modifier = Modifier.padding(8.dp),

                                    color =
                                        MaterialTheme.colorScheme.error
                                )
                            }

                            // FORMULARIO DE ASIGNACION

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                AsignarActividadScreen(
                                    formulario = vm.formulario,

                                    actividades =
                                        vm.actividadesParaAsignar,

                                    estudiantes =
                                        vm.estudiantesParaAsignar,

                                    trabajando =
                                        vm.trabajando ||
                                                vm.cargandoActividadesRemotas ||
                                                vm.cargandoEstudiantesRemotos,

                                    onModuloChange =
                                        vm::cambiarModulo,

                                    onCursoChange =
                                        vm::cambiarCurso,

                                    onDificultadChange =
                                        vm::cambiarDificultad,

                                    onActividadChange =
                                        vm::elegirActividad,

                                    onEstudianteChange =
                                        vm::elegirEstudiante,

                                    onAsignar =
                                        vm::asignarActividad
                                )
                            }
                        }
                    }

                    // SEGUIMIENTO DE ASIGNACIONES Y RESULTADOS

                    "SEGUIMIENTO" -> {

                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // MOSTRAR ERRORES DE RESULTADOS

                            if (
                                vm.errorResultadosRemotos.isNotBlank()
                            ) {

                                Text(
                                    text = vm.errorResultadosRemotos,

                                    color =
                                        MaterialTheme.colorScheme.error,

                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            // ACTUALIZAR DATOS DEL SERVIDOR

                            Row {

                                TextButton(
                                    onClick =
                                        vm::cargarAsignacionesRemotas,

                                    enabled = !vm.trabajando
                                ) {
                                    Text("Actualizar")
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                SeguimientoAsistenteScreen(
                                    asignaciones = asignaciones,
                                    resultados = resultados,
                                    trabajando = vm.trabajando,

                                    onGuardarObservacion =
                                        vm::guardarObservacion
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
