package com.example.mpazneb.model

data class EstudianteRemoto(
    val id: Int,
    val alias: String,
    val activo: Boolean
)

data class RegistroEstudianteRequest(
    val alias: String
)