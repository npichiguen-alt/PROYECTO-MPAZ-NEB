
package com.example.mpazneb.repository.administracion

import android.content.Context

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.data.local.PreguntaDao
import com.example.mpazneb.data.local.PreguntaEntity

import kotlinx.coroutines.flow.first

class PreguntaRepository(
    private val preguntaDao: PreguntaDao,
    context: Context
) {

    private val actividadDao =
        ActividadDatabase
            .obtenerInstancia(context.applicationContext)
            .actividadDao()

    private val asignacionDao =
        AsignacionDatabase
            .obtenerInstancia(context.applicationContext)
            .asignacionDao()

    // Códigos reservados para las demostraciones
    // internas del módulo Estudiante.
    private val codigosDemostracion = setOf(
        "EST-123",
        "EST-PRUEBA",
        "EST-FALLOS"
    )

    // ====================================
    // 1. VERIFICAR SI SE PUEDE MODIFICAR
    // ====================================

    private suspend fun verificarPuedeModificar(
        actividadId: Int
    ) {

        val actividad = actividadDao
            .obtenerActividades()
            .firstOrNull {
                it.id == actividadId
            }

        if (actividad == null) {
            throw IllegalStateException(
                "La actividad no existe."
            )
        }

        // No modificar preguntas de actividades activas.
        if (actividad.activa) {
            throw IllegalStateException(
                "Primero desactiva la actividad " +
                        "para modificar sus preguntas."
            )
        }

        // Consultar asignaciones guardadas en Room.
        val asignaciones =
            asignacionDao
                .observarAsignaciones()
                .first()

        val tieneAsignacionReal =
            asignaciones.any { asignacion ->

                val esDemostracionInterna =
                    asignacion.asistenteAlias ==
                            "ASISTENTE_DEMO" &&
                            asignacion.codigoTemporal in
                            codigosDemostracion

                asignacion.actividadId == actividadId &&
                        !esDemostracionInterna
            }

        if (tieneAsignacionReal) {
            throw IllegalStateException(
                "Esta actividad ya fue asignada " +
                        "a un estudiante. No puedes " +
                        "modificar sus preguntas. " +
                        "Crea una actividad nueva."
            )
        }
    }

    // ====================================
    // 2. GUARDAR UNA PREGUNTA NUEVA
    // ====================================

    suspend fun guardarPregunta(
        pregunta: PreguntaEntity
    ): Long {

        verificarPuedeModificar(
            pregunta.actividadId
        )

        return preguntaDao.insertarPregunta(
            pregunta
        )
    }

    // ====================================
    // 3. CONSULTAR PREGUNTAS
    // ====================================

    suspend fun listarPreguntas(
        actividadId: Int
    ): List<PreguntaEntity> {

        return preguntaDao
            .obtenerPreguntasPorActividad(
                actividadId
            )
    }

    // ====================================
    // 4. EDITAR UNA PREGUNTA
    // ====================================

    suspend fun editarPregunta(
        pregunta: PreguntaEntity
    ): Boolean {

        val preguntaOriginal =
            preguntaDao.obtenerPreguntaPorId(
                pregunta.id
            ) ?: return false

        if (
            preguntaOriginal.actividadId !=
            pregunta.actividadId
        ) {
            throw IllegalStateException(
                "No se puede trasladar una pregunta " +
                        "a otra actividad."
            )
        }

        verificarPuedeModificar(
            preguntaOriginal.actividadId
        )

        val filasActualizadas =
            preguntaDao.actualizarPregunta(
                pregunta
            )

        return filasActualizadas > 0
    }

    // ====================================
    // 5. ELIMINAR UNA PREGUNTA
    // ====================================

    suspend fun eliminarPregunta(
        id: Int
    ): Boolean {

        val pregunta =
            preguntaDao.obtenerPreguntaPorId(
                id
            ) ?: return false

        verificarPuedeModificar(
            pregunta.actividadId
        )

        val filasEliminadas =
            preguntaDao.eliminarPregunta(
                id
            )

        return filasEliminadas > 0
    }
}
