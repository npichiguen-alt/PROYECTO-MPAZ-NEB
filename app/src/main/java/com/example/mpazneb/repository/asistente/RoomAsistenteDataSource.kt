
package com.example.mpazneb.repository.asistente

import com.example.mpazneb.data.local.ActividadDao
import com.example.mpazneb.data.local.AsignacionDao
import com.example.mpazneb.data.local.AsignacionEntity

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.ResultadoAula

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

import com.example.mpazneb.data.local.resultadoDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest


@OptIn(ExperimentalCoroutinesApi::class)
class RoomAsistenteDataSource(
    private val actividadDao: ActividadDao,
    private val asignacionDao: AsignacionDao,
    private val resultadoDao: resultadoDao,
    private val demo: AsistenteDataSource = DemoAsistenteDataSource()
) : AsistenteDataSource {

    //ACTIVIDADES REALES CREADAS POR MATÍAS
    override val actividades: Flow<List<ActividadAula>> =
        actividadDao.observarActividadesActivas().map { lista ->

            lista.map { actividad ->
                ActividadAula(
                    id = actividad.id,
                    titulo = actividad.titulo,
                    modulo = actividad.modulo,
                    curso = actividad.curso,
                    dificultad = actividad.dificultad,
                    descripcion = actividad.descripcion,
                    estado = "Disponible"
                )
            }
        }

    //  ESTUDIANTES FICTICIOS
    // Por ahora se mantienen los estudiantes de prueba
    override val estudiantes: Flow<List<EstudianteAula>> =
        demo.estudiantes


    //  RESULTADOS REALES

    override val resultados: Flow<List<ResultadoAula>> =
        asignacionDao.observarAsignaciones().flatMapLatest { asignaciones ->

            if (asignaciones.isEmpty()) {
                flowOf(emptyList())
            } else {

                combine(
                    asignaciones.map { asignacion ->
                        resultadoDao.observarResultados(asignacion.id)
                    }
                ) { listas ->

                    listas.flatMap { it }.map { registro ->

                        ResultadoAula(
                            id = registro.id,
                            asignacionId = registro.asignacionId,
                            aciertos = registro.aciertos,
                            totalPreguntas = registro.totalPreguntas,
                            intentos = registro.intentos,
                            observacion = registro.observacion
                        )
                    }
                }
            }
        }


    // ASIGNACIONES GUARDADAS EN ROOM

    override val asignaciones: Flow<List<AsignacionAula>> =
        combine(
            asignacionDao.observarAsignaciones(),
            resultados
        ) { lista, resultadosGuardados ->

            val completadas = resultadosGuardados
                .map { it.asignacionId }
                .toSet()

            lista.map { registro ->

                AsignacionAula(
                    id = registro.id,
                    actividadId = registro.actividadId,
                    actividadTitulo = registro.actividadTitulo,
                    estudianteAlias = registro.estudianteAlias,
                    codigoTemporal = registro.codigoTemporal,
                    asistenteAlias = registro.asistenteAlias,
                    curso = registro.curso,
                    estado = if (registro.id in completadas) {
                        "Completada"
                    } else {
                        registro.estado
                    },
                    fechaAsignacion = registro.fechaAsignacion
                )
            }
        }



    //  GUARDAR UNA ASIGNACIÓN REAL EN ROOM
    override suspend fun crearAsignacion(
        item: AsignacionAula
    ) {
        val entidad = AsignacionEntity(
            actividadId = item.actividadId,
            actividadTitulo = item.actividadTitulo,
            estudianteAlias = item.estudianteAlias,
            codigoTemporal = item.codigoTemporal,
            asistenteAlias = item.asistenteAlias,
            curso = item.curso,
            estado = item.estado,
            fechaAsignacion = item.fechaAsignacion
        )

        asignacionDao.insertarAsignacion(entidad)
    }


    // GUARDAR OBSERVACIÓN PEDAGÓGICA EN ROOM
    override suspend fun guardarObservacion(
        resultadoId: Int,
        texto: String
    ) {
        val filasActualizadas = resultadoDao.actualizarObservacion(
            resultadoId = resultadoId,
            texto = texto.trim()
        )

        require(filasActualizadas > 0) {
            "No se encontró el resultado para guardar la observación."
        }
    }

}
