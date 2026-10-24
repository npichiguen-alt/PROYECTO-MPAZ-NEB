
package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.RespuestaDao
import com.example.mpazneb.data.local.RespuestaEntity
import com.example.mpazneb.model.Respuesta

class RespuestaRepository(
    private val respuestaDao: RespuestaDao
) {

    // ====================================
    // 1. GUARDAR RESPUESTA DEL ESTUDIANTE
    // ====================================

    suspend fun guardarRespuesta(
        asignacionId: Int,
        actividadId: Int,
        preguntaId: Int,
        respuestaSeleccionada: String,
        esCorrecta: Boolean,
        numeroIntento: Int
    ) {

        val nuevaRespuesta = RespuestaEntity(
            asignacionId = asignacionId,
            actividadId = actividadId,
            preguntaId = preguntaId,
            respuestaSeleccionada = respuestaSeleccionada,
            esCorrecta = esCorrecta,
            numeroIntento = numeroIntento
        )

        respuestaDao.insertarRespuesta(
            nuevaRespuesta
        )
    }

    // ====================================
    // 2. OBTENER REGISTROS DE ROOM
    // ====================================

    suspend fun obtenerRespuestas(
        asignacionId: Int
    ): List<RespuestaEntity> {

        return respuestaDao.obtenerRespuestas(
            asignacionId
        )
    }

    // ====================================
    // 3. OBTENER RESPUESTAS PARA LA PANTALLA
    // ====================================

    suspend fun obtenerRespuestasParaMostrar(
        asignacionId: Int
    ): List<Respuesta> {

        val respuestasGuardadas =
            respuestaDao.obtenerRespuestas(asignacionId)

        return respuestasGuardadas.map { registro ->

            Respuesta(
                id = registro.id,
                asignacionId = registro.asignacionId,
                actividadId = registro.actividadId,
                preguntaId = registro.preguntaId,
                respuestaSeleccionada =
                    registro.respuestaSeleccionada,
                esCorrecta = registro.esCorrecta,
                numeroIntento = registro.numeroIntento,
                fechaRespuesta = registro.fechaRespuesta
            )
        }
    }

    // ====================================
    // 4. CONTAR RESPUESTAS INCORRECTAS
    // ====================================

    suspend fun contarIncorrectas(
        asignacionId: Int
    ): Int {

        return respuestaDao.contarIncorrectas(
            asignacionId
        )
    }
}
