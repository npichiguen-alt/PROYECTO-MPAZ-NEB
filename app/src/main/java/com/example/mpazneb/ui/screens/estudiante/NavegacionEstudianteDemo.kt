
package com.example.mpazneb.ui.screens.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mpazneb.viewmodel.EstudianteViewModel

@Composable
fun NavegacionEstudianteDemo(
    estudianteViewModel: EstudianteViewModel
) {

    // DATOS DE LA ASIGNACIÓN REAL

    val estadoIngreso by
    estudianteViewModel.estado.collectAsState()

    val asignacionActiva =
        estadoIngreso.asignacionActiva

    val actividadConsultada by
    estudianteViewModel.actividadAsignada.collectAsState()

    val preguntasAsignadas by
    estudianteViewModel.preguntasAsignadas.collectAsState()

    val respuestas by
    estudianteViewModel.respuestas.collectAsState()

    val resultados by
    estudianteViewModel.resultados.collectAsState()

    val guardando by
    estudianteViewModel.guardandoRespuestaAsignada.collectAsState()

    val mensaje by
    estudianteViewModel.mensajeRespuestaAsignada.collectAsState()

    // VERIFICAR QUE LA ACTIVIDAD CORRESPONDA
    // A LA ASIGNACIÓN DEL ESTUDIANTE

    val actividadReal = actividadConsultada?.takeIf {
        it.id == asignacionActiva?.actividadId
    }

    // NAVEGACIÓN

    var pantallaActual by rememberSaveable(
        asignacionActiva?.id
    ) {
        mutableStateOf("inicio")
    }

    BackHandler(
        enabled = pantallaActual != "inicio"
    ) {
        pantallaActual = "inicio"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (pantallaActual != "inicio") {

            TextButton(
                onClick = {
                    pantallaActual = "inicio"
                }
            ) {
                Text("← Volver al inicio")
            }
        }

        when (pantallaActual) {

            // INICIO DE LA ACTIVIDAD REAL

            "inicio" -> {

                if (actividadReal != null) {

                    InicioActividadAsignadaScreen(
                        actividad = actividadReal,
                        cantidadPreguntas = preguntasAsignadas.size,

                        onAbrirActividad = {
                            pantallaActual = "contenido"
                        },

                        onAbrirResultados = {
                            pantallaActual = "resultados"
                        }
                    )

                } else {

                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        Text(
                            text = "Actividad no disponible",
                            style =
                                MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = "No se encontró la actividad " +
                                    "correspondiente a tu asignación."
                        )

                        Text(
                            text = "Consulta con tu Asistente de Aula."
                        )
                    }
                }
            }

            // RESPONDER PREGUNTAS REALES

            "contenido" -> {

                if (actividadReal != null) {

                    ActividadAsignadaScreen(
                        actividad = actividadReal,
                        preguntas = preguntasAsignadas,
                        respuestas = respuestas,
                        guardando = guardando,
                        mensaje = mensaje,

                        onResponder = { preguntaId, alternativa ->

                            estudianteViewModel
                                .responderPreguntaAsignada(
                                    preguntaId = preguntaId,
                                    alternativa = alternativa
                                )
                        },

                        onVerResultados = {
                            pantallaActual = "resultados"
                        }
                    )

                } else {

                    Text(
                        text = "La actividad no está disponible.",
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

            // RESULTADOS DEL ESTUDIANTE

            "resultados" -> {

                ResultadosEstudianteScreen(
                    resultados = resultados,
                    respuestas = respuestas,
                    actividad = actividadReal,
                    preguntas = preguntasAsignadas,
                    esDemostracion = false
                )
            }
        }
    }
}
