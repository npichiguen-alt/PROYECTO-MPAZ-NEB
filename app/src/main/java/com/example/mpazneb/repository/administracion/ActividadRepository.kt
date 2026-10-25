
package com.example.mpazneb.repository.administracion

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.data.local.ActividadDao
import com.example.mpazneb.data.local.ActividadEntity

class ActividadRepository(
    private val actividadDao: ActividadDao
) {

    // ====================================
    // 1. GUARDAR UNA ACTIVIDAD NUEVA
    // ====================================

    suspend fun guardarActividad(
        actividad: Actividad
    ) {

        val entidad = ActividadEntity(
            id = actividad.id,
            titulo = actividad.titulo,
            modulo = actividad.modulo,
            curso = actividad.curso,
            dificultad = actividad.dificultad,
            descripcion = actividad.descripcion,
            activa = actividad.activa,

            // Nuevos campos pedagógicos
            objetivo = actividad.objetivo,
            instrucciones = actividad.instrucciones
        )

        actividadDao.insertarActividad(entidad)
    }

    // ====================================
    // 2. OBTENER ACTIVIDADES DESDE ROOM
    // ====================================

    suspend fun listarActividades(): List<Actividad> {

        return actividadDao.obtenerActividades().map { entidad ->

            Actividad(
                id = entidad.id,
                titulo = entidad.titulo,
                modulo = entidad.modulo,
                curso = entidad.curso,
                dificultad = entidad.dificultad,
                descripcion = entidad.descripcion,
                activa = entidad.activa,

                // Recuperar los nuevos campos
                objetivo = entidad.objetivo,
                instrucciones = entidad.instrucciones
            )
        }
    }

    // ====================================
    // 3. ACTIVAR O DESACTIVAR ACTIVIDAD
    // ====================================

    suspend fun actualizarEstadoActividad(
        id: Int,
        activa: Boolean
    ): Boolean {

        val filasActualizadas =
            actividadDao.actualizarEstadoActividad(
                id = id,
                activa = activa
            )

        return filasActualizadas > 0
    }

    // ====================================
    // 4. EDITAR UNA ACTIVIDAD EXISTENTE
    // ====================================


    suspend fun editarActividad(
        id: Int,
        titulo: String,
        descripcion: String,
        curso: Int,
        dificultad: String,
        objetivo: String,
        instrucciones: String
    ): Boolean {

        val filasActualizadas =
            actividadDao.editarActividad(
                id = id,
                titulo = titulo,
                descripcion = descripcion,
                curso = curso,
                dificultad = dificultad,
                objetivo = objetivo,
                instrucciones = instrucciones
            )

        return filasActualizadas > 0
    }
}
