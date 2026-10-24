
package com.example.mpazneb.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resultados",
    indices = [
        Index(value = ["asignacionId"])
    ]
)
data class ResultadoEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val asignacionId: Int,

    val aciertos: Int,

    val totalPreguntas: Int,

    val intentos: Int,

    val observacion: String = "",

    val fechaRegistro: Long = System.currentTimeMillis()
)
