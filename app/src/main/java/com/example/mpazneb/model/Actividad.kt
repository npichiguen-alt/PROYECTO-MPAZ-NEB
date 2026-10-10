package com.example.mpazneb.model

data class Actividad(
    val id: Int,
    val titulo: String,
    val modulo: String,
    val curso: Int,
    val dificultad: String,
    val descripcion: String,
    val activa: Boolean = false
)
