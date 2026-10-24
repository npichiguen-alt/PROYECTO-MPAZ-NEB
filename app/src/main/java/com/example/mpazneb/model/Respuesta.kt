
package com.example.mpazneb.model

data class Respuesta(
    val id: Int,
    val asignacionId: Int,
    val actividadId: Int,
    val preguntaId: Int,
    val respuestaSeleccionada: String,
    val esCorrecta: Boolean,
    val numeroIntento: Int,
    val fechaRespuesta: Long
)
