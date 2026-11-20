package com.example.mpazneb.model

data class AsignacionRequest(
    val actividadId: Int,
    val actividadTitulo: String,
    val estudianteAlias: String,
    val asistenteAlias: String,
    val curso: Int
)

data class AsignacionRemota(
    val id: Int,
    val actividadId: Int,
    val actividadTitulo: String,
    val estudianteAlias: String,
    val codigoTemporal: String,
    val asistenteAlias: String,
    val curso: Int,
    val estado: String,
    val fechaAsignacion: Long
)
