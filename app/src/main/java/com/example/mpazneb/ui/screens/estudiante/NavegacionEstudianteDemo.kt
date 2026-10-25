
package com.example.mpazneb.ui.screens.estudiante

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mpazneb.viewmodel.EstudianteViewModel
import java.util.Locale

@Composable
fun NavegacionEstudianteDemo(
    estudianteViewModel: EstudianteViewModel
) {

    // ====================================
    // 1. PANTALLA ACTUAL
    // ====================================

    var pantallaActual by rememberSaveable {
        mutableStateOf("inicio")
    }

    // ====================================
    // 2. ASIGNACIÓN DEL ESTUDIANTE
    // ====================================

    val estadoIngreso by
    estudianteViewModel.estado.collectAsState()

    val asignacionActiva =
        estadoIngreso.asignacionActiva

    // ====================================
    // 3. CONTENIDO REAL DE ROOM
    // ====================================

    val actividadConsultada by
    estudianteViewModel.actividadAsignada.collectAsState()

    val preguntasAsignadas by
    estudianteViewModel.preguntasAsignadas.collectAsState()

    // ====================================
    // 4. CÓDIGOS DE DEMOSTRACIÓN
    // ====================================

    // Conservamos los códigos anteriores
    // para no perder las pruebas realizadas.

    val codigoActual =
        estadoIngreso.codigoTemporal
            .trim()
            .uppercase(Locale.ROOT)

    val esCodigoDemostracion =
        codigoActual in listOf(
            "EST-123",
            "EST-PRUEBA",
            "EST-FALLOS"
        )

    // ====================================
    // 5. ACTIVIDAD REAL ASIGNADA
    // ====================================

    // Se muestra únicamente la actividad
    // cuyo ID corresponde a la asignación
    // del código ingresado por el estudiante.


    val actividadReal = actividadConsultada?.takeIf { actividad ->
        !esCodigoDemostracion &&
                actividad.id == asignacionActiva?.actividadId
    }

    // ====================================
    // 6. ACTIVIDADES DEMOSTRATIVAS
    // ====================================

    val mostrarLectura =
        esCodigoDemostracion &&
                asignacionActiva?.actividadId == 1

    val mostrarMatematica =
        esCodigoDemostracion &&
                asignacionActiva?.actividadId == 2

    // ====================================
    // 7. ESTADOS DE LECTURA Y MATEMÁTICA
    // ====================================

    val estadoLectura by
    estudianteViewModel.estadoLectura.collectAsState()

    val estadoMatematica by
    estudianteViewModel.estadoMatematica.collectAsState()

    // ====================================
    // 8. RESULTADOS E HISTORIAL
    // ====================================

    val resultados by
    estudianteViewModel.resultados.collectAsState()

    val respuestas by
    estudianteViewModel.respuestas.collectAsState()

    // ====================================
    // 9. NUEVO: GUARDADO DE PREGUNTAS REALES
    // ====================================

    val guardandoRespuestaAsignada by
    estudianteViewModel
        .guardandoRespuestaAsignada
        .collectAsState()

    val mensajeRespuestaAsignada by
    estudianteViewModel
        .mensajeRespuestaAsignada
        .collectAsState()

    // ====================================
    // 10. BOTÓN ATRÁS
    // ====================================

    BackHandler(
        enabled = pantallaActual != "inicio"
    ) {
        pantallaActual = "inicio"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // ====================================
        // VOLVER AL INICIO
        // ====================================

        if (pantallaActual != "inicio") {

            TextButton(
                onClick = {
                    pantallaActual = "inicio"
                }
            ) {

                Text("← Volver al inicio")
            }
        }

        // ====================================
        // 11. NAVEGACIÓN DEL ESTUDIANTE
        // ====================================

        when (pantallaActual) {

            // ====================================
            // PANTALLA DE INICIO
            // ====================================

            "inicio" -> {

                when {

                    // No existe una asignación.
                    asignacionActiva == null -> {

                        Text(
                            text = "No hay una actividad asignada.",
                            modifier = Modifier.padding(20.dp)
                        )
                    }

                    // ====================================
                    // ACTIVIDAD REAL ENCONTRADA
                    // ====================================

                    actividadReal != null -> {

                        InicioActividadAsignadaScreen(
                            actividad = actividadReal,

                            cantidadPreguntas =
                                preguntasAsignadas.size,

                            onAbrirActividad = {
                                pantallaActual = "contenido"
                            },

                            onAbrirResultados = {
                                pantallaActual = "resultados"
                            }
                        )
                    }

                    // ====================================
                    // ACTIVIDAD NO DISPONIBLE
                    // ====================================

                    !esCodigoDemostracion -> {

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
                                text = "Tu código fue reconocido, " +
                                        "pero la actividad asignada " +
                                        "todavía no está disponible " +
                                        "en este dispositivo."
                            )

                            Text(
                                text = "Consulta con tu Asistente de Aula."
                            )

                            OutlinedButton(
                                onClick = {
                                    pantallaActual = "resultados"
                                }
                            ) {

                                Text("Mis resultados")
                            }
                        }
                    }

                    // ====================================
                    // INICIO DE DEMOSTRACIÓN
                    // ====================================

                    else -> {

                        InicioEstudianteScreen(

                            lecturaCompletada =
                                estadoLectura.finalizada,

                            matematicaCompletada =
                                estadoMatematica.finalizada,

                            lecturaAprobada =
                                estadoLectura.finalizada &&
                                        estadoLectura.esCorrecta == true,

                            matematicaAprobada =
                                estadoMatematica.finalizada &&
                                        estadoMatematica.esCorrecta == true,

                            mostrarLectura =
                                mostrarLectura,

                            mostrarMatematica =
                                mostrarMatematica,

                            onAbrirLectura = {

                                if (mostrarLectura) {
                                    pantallaActual = "lectura"
                                }
                            },

                            onAbrirMatematica = {

                                if (mostrarMatematica) {
                                    pantallaActual = "matematica"
                                }
                            },

                            onAbrirResultados = {
                                pantallaActual = "resultados"
                            }
                        )
                    }
                }
            }

            // ====================================
            // 12. PREGUNTAS REALES DE ROOM
            // ====================================

            "contenido" -> {

                if (actividadReal != null) {

                    ActividadAsignadaScreen(

                        // Datos creados por Administración
                        actividad = actividadReal,
                        preguntas = preguntasAsignadas,

                        // Respuestas recuperadas de Room
                        respuestas = respuestas,

                        // Estado de guardado
                        guardando =
                            guardandoRespuestaAsignada,

                        // Mensajes del ViewModel
                        mensaje =
                            mensajeRespuestaAsignada,

                        // ====================================
                        // NUEVO: GUARDAR RESPUESTA
                        // ====================================

                        onResponder = {
                                preguntaId,
                                alternativa ->

                            estudianteViewModel
                                .responderPreguntaAsignada(
                                    preguntaId = preguntaId,
                                    alternativa = alternativa
                                )
                        },

                        // ====================================
                        // IR A RESULTADOS
                        // ====================================

                        onVerResultados = {
                            pantallaActual = "resultados"
                        }
                    )

                } else {

                    Text(
                        text = "Esta actividad no está disponible.",
                        modifier = Modifier.padding(20.dp)
                    )

                    TextButton(
                        onClick = {
                            pantallaActual = "inicio"
                        }
                    ) {

                        Text("Volver al inicio")
                    }
                }
            }

            // ====================================
            // 13. LECTURA DE DEMOSTRACIÓN
            // ====================================

            "lectura" -> {

                if (mostrarLectura) {

                    ActividadLecturaConViewModel(
                        estudianteViewModel =
                            estudianteViewModel
                    )

                } else {

                    Text(
                        text = "Esta actividad no está asignada."
                    )

                    TextButton(
                        onClick = {
                            pantallaActual = "inicio"
                        }
                    ) {

                        Text("Volver al inicio")
                    }
                }
            }

            // ====================================
            // 14. MATEMÁTICA DE DEMOSTRACIÓN
            // ====================================

            "matematica" -> {

                if (mostrarMatematica) {

                    ActividadMatematicaConViewModel(
                        estudianteViewModel =
                            estudianteViewModel
                    )

                } else {

                    Text(
                        text = "Esta actividad no está asignada."
                    )

                    TextButton(
                        onClick = {
                            pantallaActual = "inicio"
                        }
                    ) {

                        Text("Volver al inicio")
                    }
                }
            }

            // ====================================
            // 15. RESULTADOS DEL ESTUDIANTE
            // ====================================

            "resultados" -> {

                ResultadosEstudianteScreen(
                    resultados = resultados,
                    respuestas = respuestas
                )
            }

            // ====================================
            // 16. PANTALLA DESCONOCIDA
            // ====================================

            else -> {

                TextButton(
                    onClick = {
                        pantallaActual = "inicio"
                    }
                ) {

                    Text("Ir al inicio")
                }
            }
        }
    }
}
