
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface resultadoDao {

    // Guardar el resultado de una actividad
    @Insert
    suspend fun insertarResultado(
        resultado: ResultadoEntity
    ): Long

    // Consultar resultados de una asignación
    @Query(
        "SELECT * FROM resultados " +
                "WHERE asignacionId = :asignacionId " +
                "ORDER BY fechaRegistro DESC"
    )
    suspend fun obtenerResultados(
        asignacionId: Int
    ): List<ResultadoEntity>

    // Observar resultados cuando cambian
    @Query(
        "SELECT * FROM resultados " +
                "WHERE asignacionId = :asignacionId " +
                "ORDER BY fechaRegistro DESC"
    )
    fun observarResultados(
        asignacionId: Int
    ): Flow<List<ResultadoEntity>>
}
