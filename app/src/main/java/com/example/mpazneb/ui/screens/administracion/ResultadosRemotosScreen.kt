package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.mpazneb.ui.viewmodel.ResultadosRemotosViewModel

import kotlin.math.roundToInt

@Composable
fun ResultadosRemotosScreen(
    viewModel: ResultadosRemotosViewModel = viewModel()
) {

    val resumen = viewModel.resumen

    LaunchedEffect(viewModel) {
        viewModel.cargarResultados()
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

        // ====================================
        // 1. ENCABEZADO
        // ====================================

        item {

            Text(
                text = "Resultados del servidor",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Resumen pedagogico registrado en PostgreSQL.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {

            Button(
                onClick = {
                    viewModel.cargarResultados()
                },

                enabled = !viewModel.cargando,

                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    if (viewModel.cargando) {
                        "Consultando..."
                    } else {
                        "Actualizar resultados"
                    }
                )
            }
        }

        if (viewModel.cargando) {

            item {
                CircularProgressIndicator()
            }
        }

        if (viewModel.mensaje.isNotBlank()) {

            item {

                Text(
                    text = viewModel.mensaje,

                    color = if (viewModel.errorConsulta) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }

        if (resumen != null) {

            if (viewModel.errorConsulta || viewModel.cargando) {

                item {

                    Text(
                        text = "Mostrando la ultima consulta exitosa. " +
                                "Los datos pueden estar desactualizados.",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // ====================================
            // 2. ESTADISTICAS DE ACTIVIDADES
            // ====================================

            item {

                HorizontalDivider()

                Text(
                    text = "Actividades pedagogicas",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {

                TarjetaEstadisticaRemota(
                    titulo = "Total de actividades",
                    valor = resumen.totalActividades.toString()
                )
            }

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    TarjetaEstadisticaRemota(
                        titulo = "Activas",
                        valor = resumen.actividadesActivas.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaEstadisticaRemota(
                        titulo = "Borradores",
                        valor = resumen.actividadesBorrador.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ====================================
            // 3. ASIGNACIONES
            // ====================================

            item {

                HorizontalDivider()

                Text(
                    text = "Asignaciones",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {

                TarjetaEstadisticaRemota(
                    titulo = "Total de asignaciones",
                    valor = resumen.totalAsignaciones.toString()
                )
            }

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    TarjetaEstadisticaRemota(
                        titulo = "Con resultado",
                        valor = resumen.asignacionesConResultado.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaEstadisticaRemota(
                        titulo = "Sin resultado",
                        valor = resumen.asignacionesSinResultado.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ====================================
            // 4. RENDIMIENTO PEDAGOGICO
            // ====================================

            item {

                HorizontalDivider()

                Text(
                    text = "Resultados de aprendizaje",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            item {

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    TarjetaEstadisticaRemota(
                        titulo = "Aciertos",
                        valor = resumen.totalAciertos.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    TarjetaEstadisticaRemota(
                        titulo = "Intentos",
                        valor = resumen.totalIntentos.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = "Porcentaje general de aciertos",
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (resumen.totalPreguntas > 0) {

                            Text(
                                text = "${(resumen.porcentajeGeneral * 100).roundToInt()}%",
                                style = MaterialTheme.typography.headlineMedium
                            )

                            LinearProgressIndicator(
                                progress = {
                                    resumen.porcentajeGeneral
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Text(
                                "${resumen.totalAciertos} aciertos de " +
                                "${resumen.totalPreguntas} preguntas evaluadas."
                            )

                        } else {

                            Text("Todavia no hay preguntas evaluadas.")
                        }
                    }
                }
            }

            // ====================================
            // 5. RESULTADOS INDIVIDUALES
            // ====================================

            item {

                HorizontalDivider()

                Text(
                    text = "Detalle por asignacion",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (resumen.detalles.isEmpty()) {

                item {

                    Text(
                        "Todavia no hay resultados registrados " +
                        "para las asignaciones del servidor."
                    )
                }

            } else {

                items(
                    items = resumen.detalles,
                    key = { it.asignacion.id }
                ) { detalle ->

                    val asignacion = detalle.asignacion
                    val resultado = detalle.resultado

                    val porcentaje =
                        if (resultado.totalPreguntas > 0) {
                            (
                                resultado.aciertos.toFloat() /
                                resultado.totalPreguntas.toFloat()
                            ).coerceIn(0f, 1f)
                        } else {
                            0f
                        }

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),

                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Text(
                                text = asignacion.actividadTitulo,

                                style =
                                    MaterialTheme.typography.titleLarge
                            )

                            Text(
                                "Estudiante: ${asignacion.estudianteAlias}"
                            )

                            Text(
                                "Asistente: ${asignacion.asistenteAlias}"
                            )

                            Text(
                                "Curso: ${asignacion.curso} basico"
                            )

                            detalle.actividad?.let { actividad ->

                                Text(
                                    "Modulo: ${actividad.modulo}"
                                )
                            }

                            Text(
                                "Asignacion: ${asignacion.id}"
                            )

                            Text(
                                "Estado: ${asignacion.estado}"
                            )

                            HorizontalDivider()

                            Text(
                                "Aciertos: ${resultado.aciertos} " +
                                "de ${resultado.totalPreguntas}"
                            )

                            Text(
                                "Intentos realizados: ${resultado.intentos}"
                            )

                            if (resultado.totalPreguntas > 0) {

                                Text(
                                    "Rendimiento: ${(porcentaje * 100).roundToInt()}%"
                                )

                                LinearProgressIndicator(
                                    progress = {
                                        porcentaje
                                    },

                                    modifier =
                                        Modifier.fillMaxWidth()
                                )
                            }

                            HorizontalDivider()

                            Text(
                                text = "Observacion pedagogica",

                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                resultado.observacion.ifBlank {
                                    "Todavia no hay observacion registrada."
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// ====================================
// TARJETA DE ESTADISTICA REMOTA
// ====================================

@Composable
private fun TarjetaEstadisticaRemota(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = titulo,

                style =
                    MaterialTheme.typography.bodyMedium
            )

            Text(
                text = valor,

                style =
                    MaterialTheme.typography.headlineMedium,

                color =
                    MaterialTheme.colorScheme.primary
            )
        }
    }
}