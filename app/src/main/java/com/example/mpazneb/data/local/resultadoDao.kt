
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface resultadoDao {

    // 1. GUARDAR RESULTADO
    @Insert
    suspend fun insertarResultado(
        resultado: ResultadoEntity
    ): Long

    // 2. CONSULTAR RESULTADOS DE UNA ASIGNACIÓN
    @Query(
        """
        SELECT * FROM resultados
        WHERE asignacionId = :asignacionId
        ORDER BY fechaRegistro DESC, id DESC
        """
    )
    suspend fun obtenerResultados(
        asignacionId: Int
    ): List<ResultadoEntity>

    // 3. OBSERVAR RESULTADOS DE UNA ASIGNACIÓN
    @Query(
        """
        SELECT * FROM resultados
        WHERE asignacionId = :asignacionId
        ORDER BY fechaRegistro DESC, id DESC
        """
    )
    fun observarResultados(
        asignacionId: Int
    ): Flow<List<ResultadoEntity>>

    // 4. OBSERVAR TODOS LOS RESULTADOS
    // Función utilizada por Administración
    @Query(
        """
        SELECT * FROM resultados
        ORDER BY fechaRegistro DESC, id DESC
        """
    )
    fun observarTodosLosResultados():
            Flow<List<ResultadoEntity>>

    // 5. ACTUALIZAR OBSERVACIÓN PEDAGÓGICA
    // Función utilizada por Asistente
    @Query(
        """
        UPDATE resultados
        SET observacion = :texto
        WHERE id = :resultadoId
        """
    )
    suspend fun actualizarObservacion(
        resultadoId: Int,
        texto: String
    ): Int
}
