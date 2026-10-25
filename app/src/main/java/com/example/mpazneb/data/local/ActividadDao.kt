
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActividadDao {

    // Guardar una actividad nueva
    @Insert
    suspend fun insertarActividad(
        actividad: ActividadEntity
    )

    // Consultar todas las actividades
    @Query("SELECT * FROM actividades ORDER BY id DESC")
    suspend fun obtenerActividades(): List<ActividadEntity>

    // Activar o desactivar una actividad existente
    @Query(
        "UPDATE actividades SET activa = :activa WHERE id = :id"
    )
    suspend fun actualizarEstadoActividad(
        id: Int,
        activa: Boolean
    ): Int

    @Query(
        """
    UPDATE actividades
    SET titulo = :titulo,
        descripcion = :descripcion,
        curso = :curso,
        dificultad = :dificultad,
        objetivo = :objetivo,
        instrucciones = :instrucciones
    WHERE id = :id
    """
    )
    suspend fun editarActividad(
        id: Int,
        titulo: String,
        descripcion: String,
        curso: Int,
        dificultad: String,
        objetivo: String,
        instrucciones: String
    ): Int

}
