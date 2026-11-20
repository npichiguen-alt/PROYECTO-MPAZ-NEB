package com.example.mpazneb.model.asistente

data class AsignacionRequest(
    val actividadId: Int,
    val actividadTitulo: String,
    val estudianteAlias: String,
    val asistenteAlias: String,
    val curso: Int
)