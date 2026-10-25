
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.ResultadoAula
import com.example.mpazneb.ui.theme.MPAZNEBTheme
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
@Composable
fun SeguimientoAsistenteScreen(
    asignaciones: List<AsignacionAula>,
    resultados: List<ResultadoAula>,
    trabajando: Boolean,
    onGuardarObservacion: (Int, String) -> Unit
) {
    val contexto = LocalContext.current

    // ESTADO DEL FILTRO
    var filtroEstado by rememberSaveable {
        mutableStateOf("Todas")
    }

    // CONTADORES
    val totalAsignaciones = asignaciones.size

    val completadas = asignaciones.count {
        it.estado == "Completada"
    }

    val pendientes = asignaciones.count {
        it.estado == "Pendiente"
    }

    // FILTRAR ASIGNACIONES
    val asignacionesFiltradas = asignaciones.filter {
        filtroEstado == "Todas" ||
                it.estado == filtroEstado
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        // TITULO PRINCIPAL
        item {
            TituloAsistente(
                titulo = "Seguimiento de actividades",
                descripcion = "Revisa las asignaciones y resultados de estudiantes ficticios."
            )
        }

        // RESUMEN GENERAL
        item {
            TarjetaResumenAsistente(
                titulo = "Total de asignaciones",
                contenido = "$totalAsignaciones asignaciones registradas."
            )
        }

        item {
            TarjetaResumenAsistente(
                titulo = "Actividades completadas",
                contenido = "$completadas actividades completadas."
            )
        }

        item {
            TarjetaResumenAsistente(
                titulo = "Actividades pendientes",
                contenido = "$pendientes actividades pendientes."
            )
        }

        // FILTROS
        item {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Filtrar por estado",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    modifier = Modifier.horizontalScroll(
                        rememberScrollState()
                    ),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "Todas",
                        "Pendiente",
                        "En progreso",
                        "Completada"
                    ).forEach { estado ->

                        FilterChip(
                            selected = filtroEstado == estado,
                            onClick = {
                                filtroEstado = estado
                            },
                            label = {
                                Text(estado)
                            }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Asignaciones encontradas: ${asignacionesFiltradas.size}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // MOSTRAR MENSAJE CUANDO NO HAY DATOS
        if (asignacionesFiltradas.isEmpty()) {

            item {
                Text(
                    text = "No hay asignaciones para el estado seleccionado.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        } else {

            // LISTADO DE ASIGNACIONES
            items(
                items = asignacionesFiltradas,
                key = { it.id }
            ) { asignacion ->

                // BUSCAR EL RESULTADO DE ESTA ASIGNACION
                val resultado = resultados.firstOrNull {
                    it.asignacionId == asignacion.id
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        // INFORMACION DE LA ACTIVIDAD
                        Text(
                            text = asignacion.actividadTitulo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Estudiante: ${asignacion.estudianteAlias}"
                        )

                        Text(
                            text = "Curso: ${asignacion.curso}° básico"
                        )

                        Text(
                            text = "Estado: ${asignacion.estado}"
                        )

                        Text(
                            text = "Código de asignación: ${asignacion.codigoTemporal}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            // BOTÓN PARA COPIAR EL CÓDIGO
                            OutlinedButton(
                                onClick = {

                                    val portapapeles =
                                        contexto.getSystemService(
                                            ClipboardManager::class.java
                                        )

                                    portapapeles?.setPrimaryClip(
                                        ClipData.newPlainText(
                                            "Código MPAZ NEB",
                                            asignacion.codigoTemporal
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Copiar código")
                            }

                            // BOTÓN PARA COMPARTIR EL CÓDIGO
                            Button(
                                onClick = {

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"

                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Código temporal MPAZ NEB: " +
                                                    asignacion.codigoTemporal
                                        )
                                    }

                                    contexto.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            "Compartir código"
                                        )
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Compartir")
                            }
                        }

                        HorizontalDivider()

                        // MOSTRAR LOS RESULTADOS
                        if (resultado == null) {

                            Text(
                                text = "Esta actividad todavía no tiene resultados registrados.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                        } else {

                            Text(
                                text = "Resultados",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = "Aciertos: ${resultado.aciertos} de ${resultado.totalPreguntas}"
                            )

                            Text(
                                text = "Intentos: ${resultado.intentos}"
                            )

                            // PROGRESO DE ACIERTOS
                            val progreso = if (
                                resultado.totalPreguntas > 0
                            ) {
                                (
                                        resultado.aciertos.toFloat() /
                                                resultado.totalPreguntas
                                        ).coerceIn(0f, 1f)
                            } else {
                                0f
                            }

                            LinearProgressIndicator(
                                progress = { progreso },
                                modifier = Modifier.fillMaxWidth()
                            )

                            HorizontalDivider()

                            // EDITAR OBSERVACION PEDAGOGICA
                            var observacion by rememberSaveable(
                                resultado.id,
                                resultado.observacion
                            ) {
                                mutableStateOf(resultado.observacion)
                            }

                            OutlinedTextField(
                                value = observacion,
                                onValueChange = { nuevoTexto ->
                                    if (nuevoTexto.length <= 250) {
                                        observacion = nuevoTexto
                                    }
                                },
                                label = {
                                    Text("Observación pedagógica")
                                },
                                minLines = 2,
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !trabajando
                            )

                            Text(
                                text = "${observacion.length}/250 caracteres",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Button(
                                onClick = {
                                    onGuardarObservacion(
                                        resultado.id,
                                        observacion.trim()
                                    )
                                },
                                enabled = !trabajando &&
                                        observacion.isNotBlank() &&
                                        observacion.trim() != resultado.observacion.trim(),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (trabajando) "Guardando..."
                                    else "Guardar observación"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// VISTA PREVIA PARA ANDROID STUDIO
@Preview(showBackground = true)
@Composable
fun SeguimientoAsistentePreview() {

    val asignacionesDemo = listOf(
        AsignacionAula(
            id = 101,
            actividadId = 1,
            actividadTitulo = "Cuento del bosque",
            estudianteAlias = "Estudiante A",
            codigoTemporal = "DEMO-204",
            asistenteAlias = "ASISTENTE_DEMO",
            curso = 2,
            estado = "Completada"
        ),
        AsignacionAula(
            id = 102,
            actividadId = 2,
            actividadTitulo = "Resolver sumas",
            estudianteAlias = "Estudiante B",
            codigoTemporal = "DEMO-305",
            asistenteAlias = "ASISTENTE_DEMO",
            curso = 2,
            estado = "Pendiente"
        )
    )

    var resultadosDemo by remember {
        mutableStateOf(
            listOf(
                ResultadoAula(
                    id = 50,
                    asignacionId = 101,
                    aciertos = 3,
                    totalPreguntas = 4,
                    intentos = 2,
                    observacion = "Necesita practicar comprensión de lectura."
                )
            )
        )
    }

    MPAZNEBTheme {

        SeguimientoAsistenteScreen(
            asignaciones = asignacionesDemo,
            resultados = resultadosDemo,
            trabajando = false,
            onGuardarObservacion = { id, texto ->

                // SIMULAR GUARDADO EN LA VISTA PREVIA
                resultadosDemo = resultadosDemo.map { resultado ->
                    if (resultado.id == id) {
                        resultado.copy(
                            observacion = texto.trim()
                        )
                    } else {
                        resultado
                    }
                }
            }
        )
    }
}
