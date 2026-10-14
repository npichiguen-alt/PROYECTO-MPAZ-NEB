
package com.example.mpazneb.ui.screens.asistente

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mpazneb.repository.asistente.AsistenteDataSource
import com.example.mpazneb.viewmodel.AsistenteViewModel
import com.example.mpazneb.viewmodel.AsistenteViewModelFactory

@Composable
fun AsistenteScreen(
    fuente: AsistenteDataSource
) {

    // CREAR EL VIEWMODEL DEL ASISTENTE
    val vm: AsistenteViewModel = viewModel(
        factory = remember(fuente) {
            AsistenteViewModelFactory(fuente)
        }
    )

    // OBTENER LOS DATOS DEL VIEWMODEL
    val actividades by vm.actividades.collectAsState()
    val estudiantes by vm.estudiantes.collectAsState()
    val asignaciones by vm.asignaciones.collectAsState()
    val resultados by vm.resultados.collectAsState()

    // RECORDAR LA PANTALLA ACTUAL
    var seccion by rememberSaveable {
        mutableStateOf("INICIO")
    }

    // BOTON ATRAS DEL TELEFONO
    BackHandler(enabled = seccion != "INICIO") {
        seccion = "INICIO"
    }

    // OPCIONES DEL MENU
    val opciones = listOf(
        "INICIO" to "Inicio",
        "CATALOGO" to "Catálogo",
        "ESTUDIANTES" to "Estud.",
        "ASIGNAR" to "Asignar",
        "SEGUIMIENTO" to "Seguimiento"
    )

    Scaffold(

        // BARRA INFERIOR DE NAVEGACION
        bottomBar = {

            NavigationBar {

                opciones.forEach { (clave, etiqueta) ->

                    NavigationBarItem(
                        selected = seccion == clave,

                        onClick = {
                            seccion = clave
                            vm.limpiarMensaje()
                        },

                        icon = {
                            Text(etiqueta.take(1))
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

            // MOSTRAR MENSAJES DEL VIEWMODEL
            if (vm.mensaje.isNotBlank()) {

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

            // MOSTRAR LA PANTALLA SELECCIONADA
            Box(
                modifier = Modifier.weight(1f)
            ) {

                when (seccion) {

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

                    "CATALOGO" -> {

                        CatalogoAsistenteScreen(
                            actividades = actividades,
                            onIrAsignar = {
                                seccion = "ASIGNAR"
                            }
                        )
                    }

                    "ESTUDIANTES" -> {

                        EstudiantesAsistenteScreen(
                            estudiantes = estudiantes,
                            asignaciones = asignaciones,
                            onIrAsignar = {
                                seccion = "ASIGNAR"
                            }
                        )
                    }

                    "ASIGNAR" -> {

                        AsignarActividadScreen(
                            formulario = vm.formulario,
                            actividades = actividades,
                            estudiantes = estudiantes,
                            trabajando = vm.trabajando,

                            onModuloChange = vm::cambiarModulo,
                            onCursoChange = vm::cambiarCurso,
                            onDificultadChange = vm::cambiarDificultad,
                            onActividadChange = vm::elegirActividad,
                            onEstudianteChange = vm::elegirEstudiante,

                            onAsignar = vm::asignarActividad
                        )
                    }

                    "SEGUIMIENTO" -> {

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
