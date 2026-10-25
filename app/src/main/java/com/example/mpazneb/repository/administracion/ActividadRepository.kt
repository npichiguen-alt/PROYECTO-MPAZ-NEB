
package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.local.ActividadDao
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.AsignacionDao
import com.example.mpazneb.model.Actividad

import kotlinx.coroutines.flow.first

class ActividadRepository(
    private val actividadDao: ActividadDao,
    private val asignacionDao: AsignacionDao
) {

    // Códigos internos de demostración.
    private val codigosPrueba = setOf(
        "EST-123",
        "EST-PRUEBA",
        "EST-FALLOS"
    )

    // ====================================
    // 1. GUARDAR ACTIVIDAD
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
            objetivo = actividad.objetivo,
            instrucciones = actividad.instrucciones
        )

        actividadDao.insertarActividad(entidad)
    }

    // ====================================
    // 2. LISTAR ACTIVIDADES
    // ====================================

    suspend fun listarActividades(): List<Actividad> {

        return actividadDao.obtenerActividades()
            .map { entidad ->

                Actividad(
                    id = entidad.id,
                    titulo = entidad.titulo,
                    modulo = entidad.modulo,
                    curso = entidad.curso,
                    dificultad = entidad.dificultad,
                    descripcion = entidad.descripcion,
                    activa = entidad.activa,
                    objetivo = entidad.objetivo,
                    instrucciones = entidad.instrucciones
                )
            }
    }

    // ====================================
    // 3. ACTIVAR O DESACTIVAR
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
    // 4. VERIFICAR SI PUEDE EDITARSE
    // ====================================

    private suspend fun verificarPuedeEditar(
        actividadId: Int
    ) {

        val actividad = actividadDao
            .obtenerActividades()
            .firstOrNull {
                it.id == actividadId
            }

        if (actividad == null) {
            throw IllegalStateException(
                "La actividad ya no existe."
            )
        }

        if (actividad.activa) {
            throw IllegalStateException(
                "Primero desactiva la actividad " +
                        "antes de editarla."
            )
        }

        val asignaciones = asignacionDao
            .observarAsignaciones()
            .first()

        val fueAsignada = asignaciones.any { asignacion ->

            val esPruebaInterna =
                asignacion.asistenteAlias ==
                        "ASISTENTE_DEMO" &&
                        asignacion.codigoTemporal in codigosPrueba

            asignacion.actividadId == actividadId &&
                    !esPruebaInterna
        }

        if (fueAsignada) {
            throw IllegalStateException(
                "Esta actividad ya fue asignada " +
                        "a un estudiante. Para cambiar " +
                        "su contenido, crea una nueva actividad."
            )
        }
    }

    // ====================================
    // 5. EDITAR ACTIVIDAD
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

        verificarPuedeEditar(id)

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
