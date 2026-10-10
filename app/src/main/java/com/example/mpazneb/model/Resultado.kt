package com.example.mpazneb.model

data class Resultado(
    val id: Int,
    val asignacionId: Int,
    val aciertos: Int,
    val totalPreguntas: Int,
    val intentos: Int,
    val observacion: String = ""
)
