
package com.example.mpazneb.repository.administracion

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.data.local.ActividadDao
import com.example.mpazneb.data.local.ActividadEntity

class ActividadRepository(
    private val actividadDao: ActividadDao
) {

    suspend fun guardarActividad(actividad: Actividad) {

        val entidad = ActividadEntity(
            id = actividad.id,
            titulo = actividad.titulo,
            modulo = actividad.modulo,
            curso = actividad.curso,
            dificultad = actividad.dificultad,
            descripcion = actividad.descripcion,
            activa = actividad.activa
        )

        actividadDao.insertarActividad(entidad)
    }

    suspend fun listarActividades(): List<ActividadEntity> {
        return actividadDao.obtenerActividades()
    }
}

