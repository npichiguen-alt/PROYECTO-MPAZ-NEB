package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RespuestaDao {

    // ====================================
    // GUARDAR RESPUESTA
    // ====================================

    @Insert
    suspend fun insertarRespuesta(
        respuesta: RespuestaEntity
    ): Long

    // ====================================
    // OBTENER RESPUESTAS
    // ====================================

    @Query(
        """
        SELECT * FROM respuestas
        WHERE asignacionId = :asignacionId
        ORDER BY fechaRespuesta ASC, id ASC
        """
    )
    suspend fun obtenerRespuestas(
        asignacionId: Int
    ): List<RespuestaEntity>

    // ====================================
    // CONTAR RESPUESTAS INCORRECTAS
    // ====================================

    @Query(
        """
        SELECT COUNT(*) FROM respuestas
        WHERE asignacionId = :asignacionId
        AND esCorrecta = 0
        """
    )
    suspend fun contarIncorrectas(
        asignacionId: Int
    ): Int

    // ====================================
    // COMPROBAR RESULTADO FINAL EXISTENTE
    // ====================================

    @Query(
        """
        SELECT EXISTS(
            SELECT 1 FROM resultados
            WHERE asignacionId = :asignacionId
        )
        """
    )
    suspend fun existeResultadoFinal(
        asignacionId: Int
    ): Boolean

    // ====================================
    // INSERTAR RESULTADO FINAL
    // ====================================

    @Insert
    suspend fun insertarResultadoFinal(
        resultado: ResultadoEntity
    ): Long

    // ====================================
    // GUARDAR RESPUESTA Y RESULTADO
    // EN UNA TRANSACCIÓN ROOM
    // ====================================

    @Transaction
    suspend fun guardarRespuestaYResultado(
        asignacionId: Int,
        actividadId: Int,
        preguntaId: Int,
        respuestaSeleccionada: String,
        esCorrecta: Boolean,
        preguntasIds: List<Int>
    ): Boolean {

        // Validar datos de entrada.

        require(asignacionId > 0)
        require(actividadId > 0)
        require(preguntaId in preguntasIds)
        require(
            respuestaSeleccionada in
                    listOf("A", "B", "C", "D")
        )

        // No permitir nuevas respuestas
        // si la actividad tiene resultado final.

        if (existeResultadoFinal(asignacionId)) {
            return false
        }

        // Recuperar respuestas anteriores.

        val anteriores =
            obtenerRespuestas(asignacionId)

        val historialPregunta =
            anteriores.filter {
                it.actividadId == actividadId &&
                        it.preguntaId == preguntaId
            }

        val yaCorrecta =
            historialPregunta.any {
                it.esCorrecta
            }

        val intentosUsados =
            historialPregunta.maxOfOrNull {
                it.numeroIntento
            } ?: 0

        // Máximo tres intentos por pregunta.

        if (yaCorrecta || intentosUsados >= 3) {
            return false
        }

        // ====================================
        // GUARDAR NUEVO INTENTO
        // ====================================

        insertarRespuesta(
            RespuestaEntity(
                asignacionId = asignacionId,
                actividadId = actividadId,
                preguntaId = preguntaId,
                respuestaSeleccionada =
                    respuestaSeleccionada,
                esCorrecta = esCorrecta,
                numeroIntento = intentosUsados + 1
            )
        )

        // ====================================
        // VERIFICAR PREGUNTAS FINALIZADAS
        // ====================================

        val respuestasActualizadas =
            obtenerRespuestas(asignacionId)

        val todasFinalizadas =
            preguntasIds.isNotEmpty() &&
                    preguntasIds.all { idPregunta ->

                        val intentos =
                            respuestasActualizadas.filter {
                                it.actividadId == actividadId &&
                                        it.preguntaId == idPregunta
                            }

                        val acertada =
                            intentos.any {
                                it.esCorrecta
                            }

                        val cantidadIntentos =
                            intentos.maxOfOrNull {
                                it.numeroIntento
                            } ?: 0

                        acertada || cantidadIntentos >= 3
                    }

        // ====================================
        // GUARDAR RESULTADO SI TERMINÓ
        // ====================================

        if (todasFinalizadas) {

            val aciertos =
                preguntasIds.count { idPregunta ->

                    respuestasActualizadas.any {
                        it.actividadId == actividadId &&
                                it.preguntaId == idPregunta &&
                                it.esCorrecta
                    }
                }

            val totalIntentos =
                respuestasActualizadas.count {
                    it.actividadId == actividadId &&
                            it.preguntaId in preguntasIds
                }

            insertarResultadoFinal(
                ResultadoEntity(
                    asignacionId = asignacionId,
                    aciertos = aciertos,
                    totalPreguntas = preguntasIds.size,
                    intentos = totalIntentos,
                    observacion = ""
                )
            )
        }

        return true
    }
}
