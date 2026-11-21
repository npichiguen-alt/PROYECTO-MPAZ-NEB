package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient

class ContenidoEstudianteRepository(
    private val database: ActividadDatabase,
    private val api: MpazApi = RetrofitClient.api
) {

    suspend fun buscarActividad(
        actividadId: Int
    ): ActividadEntity? {
        return try {
            val actividad = api.obtenerActividadPorId(actividadId)
            ActividadEntity(
                id = actividad.id,
                titulo = actividad.titulo,
                modulo = actividad.modulo,
                curso = actividad.curso,
                dificultad = actividad.dificultad,
                descripcion = actividad.descripcion,
                activa = actividad.activa,
                objetivo = actividad.objetivo,
                instrucciones = actividad.instrucciones
            )
        } catch (_: Exception) {
            database.actividadDao()
                .obtenerActividades()
                .firstOrNull { actividad ->
                    actividad.id == actividadId
                }
        }
    }

    suspend fun obtenerPreguntas(
        actividadId: Int
    ): List<PreguntaEntity> {
        return try {
            api.obtenerPreguntasPorActividad(actividadId)
        } catch (_: Exception) {
            database.preguntaDao()
                .obtenerPreguntasPorActividad(
                    actividadId
                )
        }
    }
}
