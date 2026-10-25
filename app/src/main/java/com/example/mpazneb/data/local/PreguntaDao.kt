
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PreguntaDao {

    // Agregar una pregunta
    @Insert
    suspend fun insertarPregunta(
        pregunta: PreguntaEntity
    ): Long

    // Obtener preguntas de una actividad
    @Query(
        """
        SELECT * FROM preguntas
        WHERE actividadId = :actividadId
        ORDER BY id ASC
        """
    )
    suspend fun obtenerPreguntasPorActividad(
        actividadId: Int
    ): List<PreguntaEntity>

    // Editar una pregunta existente
    @Update
    suspend fun actualizarPregunta(
        pregunta: PreguntaEntity
    ): Int

    // Eliminar una pregunta específica
    @Query(
        "DELETE FROM preguntas WHERE id = :id"
    )
    suspend fun eliminarPregunta(
        id: Int
    ): Int
}
