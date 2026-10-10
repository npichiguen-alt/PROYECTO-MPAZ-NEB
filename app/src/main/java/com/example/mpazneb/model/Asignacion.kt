package com.example.mpazneb.model

data class Asignacion(
    val id: Int,
    val actividadId: Int,
    val estudianteFicticio: String,
    val codigoTemporal: String,
    val estado: String = "Pendiente"
)
