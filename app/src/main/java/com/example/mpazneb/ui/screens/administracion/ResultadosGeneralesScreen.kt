
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.data.local.ResultadoEntity
import com.example.mpazneb.model.Actividad

import kotlin.math.roundToInt

// ====================================
// RESULTADOS GENERALES
// ADMINISTRACIÓN PEDAGÓGICA
// ====================================

@Composable
fun ResultadosGeneralesScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier
) {

    // ====================================
    // 1. CONECTAR CON ROOM
    // ====================================

    val contexto =
        LocalContext.current.applicationContext

    val database = remember(contexto) {
        AsignacionDatabase.obtenerInstancia(contexto)
    }

    // Asignaciones realizadas por el Asistente.
    val flujoAsignaciones = remember(database) {
        database.asignacionDao().observarAsignaciones()
    }

    val asignaciones by flujoAsignaciones.collectAsState(
        initial = emptyList()
    )

    // Resultados guardados por el Estudiante.
    val flujoResultados = remember(database) {
        database.resultadoDao()
            .observarTodosLosResultados()
    }

    val resultadosGuardados by
    flujoResultados.collectAsState(
        initial = emptyList()
    )

    // ====================================
    // 2. ESTADÍSTICAS DE ACTIVIDADES
    // ====================================

    val totalActividades = actividades.size

    val actividadesLectura = actividades.count {
        it.modulo == "Lectura"
    }

    val actividadesMatematica = actividades.count {
        it.modulo == "Matemática"
    }

    val actividadesActivas = actividades.count {
        it.activa
    }

    val actividadesBorrador = actividades.count {
        !it.activa
    }

    val actividadesPorId = actividades.associateBy {
        it.id
    }

    // ====================================
    // 3. RELACIONAR RESULTADOS
    // CON ASIGNACIONES
    // ====================================

    val asignacionesPorId = asignaciones.associateBy {
        it.id
    }

    // Si una asignación tiene varios resultados
    // históricos, mostrar el último registrado.
    //
    // Se conserva el historial en Room.
    val resultadosFinales =
        resultadosGuardados
            .sortedWith(
                compareByDescending<ResultadoEntity> {
                    it.fechaRegistro
                }.thenByDescending {
                    it.id
                }
            )
            .distinctBy {
                it.asignacionId
            }
            .mapNotNull { resultado ->

                val asignacion =
                    asignacionesPorId[resultado.asignacionId]

                if (asignacion != null) {
                    asignacion to resultado
                } else {
                    null
                }
            }

    // ====================================
    // 4. RESUMEN DE APRENDIZAJE
    // ====================================

    val cantidadResultados = resultadosFinales.size

    val totalAciertos = resultadosFinales.sumOf {
        it.second.aciertos
    }

    val totalPreguntas = resultadosFinales.sumOf {
        it.second.totalPreguntas
    }

    val totalIntentos = resultadosFinales.sumOf {
        it.second.intentos
    }

    val porcentajeGeneral =
        if (totalPreguntas > 0) {
            (
                    totalAciertos.toFloat() /
                            totalPreguntas.toFloat()
                    ).coerceIn(0f, 1f)
        } else {
            0f
        }

    val codigosDemostracion = setOf(
        "EST-123",
        "EST-PRUEBA",
        "EST-FALLOS"
    )

    val cantidadPruebas = resultadosFinales.count {
        it.first.codigoTemporal in codigosDemostracion
    }

    // ====================================
    // 5. PANTALLA
    // ====================================

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement =
            Arrangement.spacedBy(12.dp),

        contentPadding =
            PaddingValues(bottom = 24.dp)
    ) {

        // ====================================
        // ENCABEZADO
        // ====================================

        item {

            Text(
                text = "Resultados generales",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {

            Text(
                text = "Resumen de actividades y " +
                        "resultados registrados en Room.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // ====================================
        // ACTIVIDADES PEDAGÓGICAS
        // ====================================

        item {

            Text(
                text = "Actividades pedagógicas",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            TarjetaEstadisticaAdministracion(
                titulo = "Total de actividades",
                valor = totalActividades.toString()
            )
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Lectura",
                        valor = actividadesLectura.toString()
                    )
                }

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Matemática",
                        valor = actividadesMatematica.toString()
                    )
                }
            }
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Activas",
                        valor = actividadesActivas.toString()
                    )
                }

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Borradores",
                        valor = actividadesBorrador.toString()
                    )
                }
            }
        }

        // ====================================
        // RESUMEN DE RESULTADOS
        // ====================================

        item {

            HorizontalDivider()
        }

        item {

            Text(
                text = "Resultados de aprendizaje",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {

            TarjetaEstadisticaAdministracion(
                titulo = "Asignaciones con resultado",
                valor = cantidadResultados.toString()
            )
        }

        item {

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp),

                modifier = Modifier.fillMaxWidth()
            ) {

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Aciertos",
                        valor = totalAciertos.toString()
                    )
                }

                Box(modifier = Modifier.weight(1f)) {

                    TarjetaEstadisticaAdministracion(
                        titulo = "Intentos",
                        valor = totalIntentos.toString()
                    )
                }
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

                    Text(
                        text = "${(porcentajeGeneral * 100).roundToInt()}%",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    LinearProgressIndicator(
                        progress = {
                            porcentajeGeneral
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "$totalAciertos respuestas correctas " +
                                "de $totalPreguntas preguntas evaluadas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (cantidadPruebas > 0) {

            item {

                Text(
                    text = "Hay $cantidadPruebas resultado(s) " +
                            "de los códigos de demostración. " +
                            "Están incluidos en el resumen.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // ====================================
        // DETALLE DE CADA RESULTADO
        // ====================================

        item {

            HorizontalDivider()
        }

        item {

            Text(
                text = "Detalle por asignación",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (resultadosFinales.isEmpty()) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Todavía no hay resultados",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Los resultados aparecerán cuando " +
                                    "un estudiante finalice una actividad " +
                                    "y la guarde en Room."
                        )

                        Text(
                            text = "Para ver los resultados de Katy " +
                                    "y Noemí, los módulos deben usar " +
                                    "la misma instalación de la aplicación.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

        } else {

            items(
                items = resultadosFinales,
                key = { par -> par.second.id }
            ) { (asignacion, resultado) ->

                val actividad =
                    actividadesPorId[asignacion.actividadId]

                val esDemostracion =
                    asignacion.codigoTemporal in
                            codigosDemostracion

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
                            Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = asignacion.actividadTitulo,
                            style = MaterialTheme.typography.titleLarge
                        )

                        if (esDemostracion) {

                            Text(
                                text = "Resultado de demostración",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        Text(
                            text = "Estudiante: " +
                                    asignacion.estudianteAlias
                        )

                        Text(
                            text = "Curso: " +
                                    "${asignacion.curso}° básico"
                        )

                        if (!esDemostracion && actividad != null) {

                            Text(
                                text = "Módulo: ${actividad.modulo}"
                            )
                        }

                        Text(
                            text = "Asignación N.º ${asignacion.id}",
                            style = MaterialTheme.typography.bodySmall
                        )

                        HorizontalDivider()

                        Text(
                            text = "Aciertos: " +
                                    "${resultado.aciertos} de " +
                                    "${resultado.totalPreguntas}"
                        )

                        Text(
                            text = "Intentos realizados: " +
                                    resultado.intentos
                        )

                        Text(
                            text = "Porcentaje de aciertos: " +
                                    "${(porcentaje * 100).roundToInt()}%"
                        )

                        LinearProgressIndicator(
                            progress = {
                                porcentaje
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        HorizontalDivider()

                        Text(
                            text = "Observación o nota registrada",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = resultado.observacion.ifBlank {
                                "Todavía no hay observación registrada."
                            }
                        )
                    }
                }
            }
        }
    }
}

// ====================================
// TARJETA DE ESTADÍSTICA
// ====================================

@Composable
private fun TarjetaEstadisticaAdministracion(
    titulo: String,
    valor: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),

            verticalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
