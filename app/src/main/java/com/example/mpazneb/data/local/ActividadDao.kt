
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
@Dao
interface ActividadDao {

    // ====================================
    // 1. GUARDAR ACTIVIDAD
    // ====================================

    @Insert
    suspend fun insertarActividad(
        actividad: ActividadEntity
    )

    // ====================================
    // 2. LISTAR ACTIVIDADES
    // ====================================

    @Query(
        "SELECT * FROM actividades ORDER BY id DESC"
    )
    suspend fun obtenerActividades(): List<ActividadEntity>

    // ====================================
    // 3. ACTIVAR O DESACTIVAR ACTIVIDAD
    // ====================================

    // Solo permite activar si existe
    // al menos una pregunta asociada.
    //
    // Desactivar siempre está permitido.

    @Query(
        """
        UPDATE actividades
        SET activa = :activa
        WHERE id = :id
        AND (
            :activa = 0
            OR EXISTS (
                SELECT 1
                FROM preguntas
                WHERE actividadId = :id
            )
        )
        """
    )
    suspend fun actualizarEstadoActividad(
        id: Int,
        activa: Boolean
    ): Int

    // ====================================
    // 4. EDITAR ACTIVIDAD
    // ====================================

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
    @Query("""
    SELECT * FROM actividades
    WHERE activa = 1
    AND EXISTS (
        SELECT 1 FROM preguntas
        WHERE preguntas.actividadId = actividades.id
    )
    ORDER BY id DESC
""")
    fun observarActividadesActivas(): Flow<List<ActividadEntity>>
}

