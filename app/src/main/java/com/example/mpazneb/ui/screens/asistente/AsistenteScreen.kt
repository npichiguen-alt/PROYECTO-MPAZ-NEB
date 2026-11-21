
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
    val estudiantes by vm.estudiantes.collectAsState()

    // DATOS REMOTOS DE AWS
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

    // CARGAR ACTIVIDADES DE AWS CUANDO CORRESPONDA
    LaunchedEffect(seccion, origenCatalogo) {
        if (
            seccion == "CATALOGO" &&
            origenCatalogo == "AWS"
        ) {
            vm.cargarActividadesRemotas()
        }
    }

    // ACTUALIZAR ASIGNACIONES REMOTAS
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
                            actividades = actividades,
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
                                modifier = Modifier.padding(8.dp),
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

                                        if (origenCatalogo == "AWS") {
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

                    // LISTADO DE ESTUDIANTES
                    "ESTUDIANTES" -> {

                        EstudiantesAsistenteScreen(
                            estudiantes = estudiantes,
                            asignaciones = asignaciones,

                            onIrAsignar = {
                                seccion = "ASIGNAR"
                            }
                        )
                    }

                    // ASIGNAR ACTIVIDADES DESDE AWS
                    "ASIGNAR" -> {

                        LaunchedEffect(Unit) {
                            vm.cargarActividadesRemotas()
                        }

                        Column {

                            if (vm.cargandoActividadesRemotas) {

                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

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
                                    onClick = vm::cargarActividadesRemotas
                                ) {
                                    Text("Reintentar")
                                }
                            }

                            Box(
                                modifier = Modifier.weight(1f)
                            ) {

                                AsignarActividadScreen(
                                    formulario = vm.formulario,
                                    actividades = vm.actividadesParaAsignar,
                                    estudiantes = estudiantes,

                                    trabajando =
                                        vm.trabajando ||
                                                vm.cargandoActividadesRemotas,

                                    onModuloChange = vm::cambiarModulo,
                                    onCursoChange = vm::cambiarCurso,
                                    onDificultadChange =
                                        vm::cambiarDificultad,
                                    onActividadChange = vm::elegirActividad,
                                    onEstudianteChange = vm::elegirEstudiante,

                                    onAsignar = vm::asignarActividad
                                )
                            }
                        }
                    }

                    // SEGUIMIENTO DE ASIGNACIONES Y RESULTADOS
                    "SEGUIMIENTO" -> {

                        Column {

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
                                    onClick = vm::cargarAsignacionesRemotas,
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
