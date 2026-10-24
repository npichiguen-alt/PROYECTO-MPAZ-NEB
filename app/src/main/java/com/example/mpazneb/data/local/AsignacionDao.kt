
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignacionDao {

    // Guardar una asignación del asistente
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarAsignacion(
        asignacion: AsignacionEntity
    ): Long

    // Buscar una asignación por su código
    @Query(
        "SELECT * FROM asignaciones " +
                "WHERE codigoTemporal = :codigo LIMIT 1"
    )
    suspend fun buscarPorCodigo(
        codigo: String
    ): AsignacionEntity?

    // Observar todas las asignaciones guardadas
    @Query(
        "SELECT * FROM asignaciones " +
                "ORDER BY fechaAsignacion DESC"
    )
    fun observarAsignaciones(): Flow<List<AsignacionEntity>>

}
