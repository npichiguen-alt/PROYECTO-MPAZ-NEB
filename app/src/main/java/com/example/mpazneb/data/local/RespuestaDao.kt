package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RespuestaDao {

    // Guardar una respuesta del estudiante
    @Insert
    suspend fun insertarRespuesta(
        respuesta: RespuestaEntity
    ): Long

    // Obtener las respuestas de una asignación
    @Query(
        "SELECT * FROM respuestas " +
                "WHERE asignacionId = :asignacionId " +
                "ORDER BY fechaRespuesta ASC, id ASC"
    )
    suspend fun obtenerRespuestas(
        asignacionId: Int
    ): List<RespuestaEntity>

    // Contar las respuestas incorrectas
    @Query(
        "SELECT COUNT(*) FROM respuestas " +
                "WHERE asignacionId = :asignacionId " +
                "AND esCorrecta = 0"
    )
    suspend fun contarIncorrectas(
        asignacionId: Int
    ): Int
}
