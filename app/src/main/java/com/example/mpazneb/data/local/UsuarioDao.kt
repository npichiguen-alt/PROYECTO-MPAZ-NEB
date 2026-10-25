
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // Consultar todos los usuarios.
    // Room actualiza la lista cuando cambian.
    @Query(
        """
        SELECT * FROM usuarios_ficticios
        ORDER BY alias COLLATE NOCASE ASC
        """
    )
    fun observarUsuarios(): Flow<List<UsuarioEntity>>

    // Crear un usuario ficticio.
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    ): Long

    // Activar o desactivar sin borrar historial.
    @Query(
        """
        UPDATE usuarios_ficticios
        SET activo = :activo
        WHERE id = :id
        """
    )
    suspend fun actualizarEstado(
        id: Int,
        activo: Boolean
    ): Int
}
