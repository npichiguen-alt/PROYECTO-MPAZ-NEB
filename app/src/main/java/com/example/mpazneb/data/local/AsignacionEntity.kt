package com.example.mpazneb.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "asignaciones",
    indices = [
        Index(value = ["codigoTemporal"], unique = true)
    ]
)
data class AsignacionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val actividadId: Int,

    val actividadTitulo: String,

    val estudianteAlias: String,

    val codigoTemporal: String,

    val asistenteAlias: String,

    val curso: Int,

    val estado: String = "Pendiente",

    val fechaAsignacion: Long = System.currentTimeMillis()
)
