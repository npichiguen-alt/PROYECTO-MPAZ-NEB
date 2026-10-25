package com.example.mpazneb.repository.asistente


import com.example.mpazneb.data.local.ActividadDao
import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.ResultadoAula

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomAsistenteDataSource(
    private val actividadDao: ActividadDao,
    private val demo: AsistenteDataSource = DemoAsistenteDataSource()
) : AsistenteDataSource {

    //  ACTIVIDADES REALES DE ADMINISTRACIÓN
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
    // Por ahora mantenemos los datos de demostración.
    override val estudiantes: Flow<List<EstudianteAula>> =
        demo.estudiantes

    //  ASIGNACIONES
    // Se conectarán con Room en el siguiente avance.
    override val asignaciones: Flow<List<AsignacionAula>> =
        demo.asignaciones

    //  RESULTADOS
    // Se conectarán después con el módulo Estudiante.
    override val resultados: Flow<List<ResultadoAula>> =
        demo.resultados

    //  CREAR ASIGNACIÓN
    override suspend fun crearAsignacion(
        item: AsignacionAula
    ) {
        demo.crearAsignacion(item)
    }

    // GUARDAR OBSERVACIÓN
    override suspend fun guardarObservacion(
        resultadoId: Int,
        texto: String
    ) {
        demo.guardarObservacion(
            resultadoId,
            texto
        )
    }
}
