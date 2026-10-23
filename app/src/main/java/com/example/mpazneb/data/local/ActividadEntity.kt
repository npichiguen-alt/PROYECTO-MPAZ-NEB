package com.example.mpazneb.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "actividades")
data class ActividadEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val titulo: String,
    val modulo: String,
    val curso: Int,
    val dificultad: String,
    val descripcion: String,
    val activa: Boolean = false
)