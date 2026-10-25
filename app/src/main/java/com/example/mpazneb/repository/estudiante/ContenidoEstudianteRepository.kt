package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity

class ContenidoEstudianteRepository(
    private val database: ActividadDatabase
) {

    // Buscar la actividad que asignó el Asistente.
    // Utilizamos el DAO que ya creó Administración.
    suspend fun buscarActividad(
        actividadId: Int
    ): ActividadEntity? {

        return database.actividadDao()
            .obtenerActividades()
            .firstOrNull { actividad ->
                actividad.id == actividadId
            }
    }

    // Recuperar únicamente las preguntas
    // correspondientes a esa actividad.
    suspend fun obtenerPreguntas(
        actividadId: Int
    ): List<PreguntaEntity> {

        return database.preguntaDao()
            .obtenerPreguntasPorActividad(
                actividadId
            )
    }
}
