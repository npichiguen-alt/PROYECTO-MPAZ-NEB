package com.example.mpazneb.data.local
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "respuestas",
    indices = [
        Index(value = ["asignacionId"])
    ]
)
data class RespuestaEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Asignación del estudiante
    val asignacionId: Int,

    // Actividad que está realizando
    val actividadId: Int,

    // Pregunta que respondió
    val preguntaId: Int,

    // Alternativa seleccionada
    val respuestaSeleccionada: String,

    // Indica si respondió correctamente
    val esCorrecta: Boolean,

    // Número de intento
    val numeroIntento: Int,

    // Fecha y hora de la respuesta
    val fechaRespuesta: Long = System.currentTimeMillis()
)
