
package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.local.PreguntaDao
import com.example.mpazneb.data.local.PreguntaEntity

class PreguntaRepository(
    private val preguntaDao: PreguntaDao
) {

    // ====================================
    // 1. GUARDAR UNA PREGUNTA
    // ====================================

    suspend fun guardarPregunta(
        pregunta: PreguntaEntity
    ): Long {

        return preguntaDao.insertarPregunta(
            pregunta
        )
    }

    // ====================================
    // 2. CONSULTAR PREGUNTAS
    // DE UNA ACTIVIDAD
    // ====================================

    suspend fun listarPreguntas(
        actividadId: Int
    ): List<PreguntaEntity> {

        return preguntaDao.obtenerPreguntasPorActividad(
            actividadId
        )
    }

    // ====================================
    // 3. EDITAR UNA PREGUNTA
    // ====================================

    suspend fun editarPregunta(
        pregunta: PreguntaEntity
    ): Boolean {

        val filasActualizadas =
            preguntaDao.actualizarPregunta(
                pregunta
            )

        return filasActualizadas > 0
    }

    // ====================================
    // 4. ELIMINAR UNA PREGUNTA
    // ====================================

    suspend fun eliminarPregunta(
        id: Int
    ): Boolean {

        val filasEliminadas =
            preguntaDao.eliminarPregunta(id)

        return filasEliminadas > 0
    }
}
