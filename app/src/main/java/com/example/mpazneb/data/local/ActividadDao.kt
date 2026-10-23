
package com.example.mpazneb.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActividadDao {

    @Insert
    suspend fun insertarActividad(
        actividad: ActividadEntity
    )

    @Query("SELECT * FROM actividades ORDER BY id DESC")
    suspend fun obtenerActividades(): List<ActividadEntity>
}
