
package com.example.mpazneb.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preguntas",
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["actividadId"])
    ]
)
data class PreguntaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Actividad a la que pertenece
    val actividadId: Int,

    // Enunciado de la pregunta
    val enunciado: String,

    // Cuatro alternativas
    val alternativaA: String,
    val alternativaB: String,
    val alternativaC: String,
    val alternativaD: String,

    // Respuesta correcta: A, B, C o D
    val respuestaCorrecta: String,

    // Mensaje para el estudiante
    val retroalimentacion: String
)
