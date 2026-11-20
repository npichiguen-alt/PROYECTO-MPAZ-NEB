package com.example.mpazneb.model

data class ActividadRequest(
    val titulo: String,
    val modulo: String,
    val curso: Int,
    val dificultad: String,
    val descripcion: String,
    val objetivo: String,
    val instrucciones: String
)