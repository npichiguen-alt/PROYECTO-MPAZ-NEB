
package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.RespuestaDao
import com.example.mpazneb.data.local.RespuestaEntity
import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.model.RespuestaRequest

class RespuestaRepository(
    private val respuestaDao: RespuestaDao,
    private val api: MpazApi = RetrofitClient.api
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

        return try {
            val remotas = api.obtenerRespuestasPorAsignacion(asignacionId)
            try {
                val existentes = respuestaDao.obtenerRespuestas(asignacionId)
                remotas.forEach { resp ->
                    val existe = existentes.any {
                        it.actividadId == resp.actividadId &&
                                it.preguntaId == resp.preguntaId &&
                                it.numeroIntento == resp.numeroIntento
                    }
                    if (!existe) {
                        respuestaDao.insertarRespuesta(
                            RespuestaEntity(
                                asignacionId = resp.asignacionId,
                                actividadId = resp.actividadId,
                                preguntaId = resp.preguntaId,
                                respuestaSeleccionada = resp.respuestaSeleccionada,
                                esCorrecta = resp.esCorrecta,
                                numeroIntento = resp.numeroIntento,
                                fechaRespuesta = resp.fechaRespuesta
                            )
                        )
                    }
                }
            } catch (_: Exception) {
            }
            remotas
        } catch (_: Exception) {
            val respuestasGuardadas =
                respuestaDao.obtenerRespuestas(asignacionId)

            respuestasGuardadas.map { registro ->
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


    // ====================================
    // GUARDADO SEGURO DE RESPUESTA Y RESULTADO
    // ====================================

    suspend fun guardarRespuestaYResultado(
        asignacionId: Int,
        actividadId: Int,
        preguntaId: Int,
        respuestaSeleccionada: String,
        esCorrecta: Boolean,
        preguntasIds: List<Int>
    ): Boolean {
        val respuestaRemota = try {
            api.registrarRespuesta(
                RespuestaRequest(
                    asignacionId = asignacionId,
                    actividadId = actividadId,
                    preguntaId = preguntaId,
                    respuestaSeleccionada = respuestaSeleccionada
                )
            )
        } catch (_: Exception) {
            null
        }

        if (respuestaRemota == null) {
            throw IllegalStateException("No se pudo guardar la respuesta en el servidor")
        }

        val registrada = respuestaDao.guardarRespuestaYResultado(
            asignacionId = asignacionId,
            actividadId = actividadId,
            preguntaId = preguntaId,
            respuestaSeleccionada = respuestaSeleccionada,
            esCorrecta = respuestaRemota.esCorrecta,
            preguntasIds = preguntasIds
        )

        if (registrada) {
            try {
                api.calcularResultado(asignacionId)
            } catch (_: Exception) {
            }
        }

        return registrada
    }

}
