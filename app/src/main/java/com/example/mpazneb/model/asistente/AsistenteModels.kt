package com.example.mpazneb.model.asistente



data class ActividadAula(
    val id: Int,
    val titulo: String,
    val modulo: String,
    val curso: Int,
    val dificultad: String,
    val descripcion: String,
    val estado: String
)

data class EstudianteAula(
    val alias: String,
    val activo: Boolean = true
)

data class AsignacionAula(
    val id: Int = 0,
    val actividadId: Int,
    val actividadTitulo: String,
    val estudianteAlias: String,
    val codigoTemporal: String,
    val asistenteAlias: String,
    val curso: Int,
    val estado: String = "Pendiente",
    val fechaAsignacion: Long = System.currentTimeMillis()
)

data class ResultadoAula(
    val id: Int,
    val asignacionId: Int,
    val aciertos: Int,
    val totalPreguntas: Int,
    val intentos: Int,
    val observacion: String
)

data class FormularioAsignacion(
    val modulo: String = "Lectura",
    val curso: Int = 2,
    val dificultad: String = "Básica",
    val actividadId: Int = 0,
    val estudianteAlias: String = ""
)

object ValidadorAsignacion {

    fun validar(
        form: FormularioAsignacion,
        actividades: List<ActividadAula>,
        estudiantes: List<EstudianteAula>
    ): String? {

        if (form.modulo !in listOf("Lectura", "Matemática")) {
            return "Módulo inválido."
        }

        if (form.curso !in 1..4) {
            return "Selecciona un curso entre 1.º y 4.º."
        }

        if (form.dificultad !in
            listOf("Básica", "Media", "Avanzada")
        ) {
            return "Selecciona una dificultad válida."
        }

        val actividad = actividades.firstOrNull {
            it.id == form.actividadId
        } ?: return "Selecciona una actividad."

        if (
            actividad.estado != "Disponible" ||
            actividad.modulo != form.modulo ||
            actividad.curso != form.curso ||
            actividad.dificultad != form.dificultad
        ) {
            return "La actividad seleccionada no está disponible para esos filtros."
        }

        if (form.estudianteAlias.isBlank()) {
            return "Selecciona un estudiante ficticio."
        }

        if (estudiantes.none {
                it.alias == form.estudianteAlias && it.activo
            }
        ) {
            return "El estudiante no existe o no está activo."
        }

        return null
    }

    fun validarObservacion(texto: String): String? {
        if (texto.isBlank()) {
            return "Escribe una observación ficticia."
        }

        if (texto.trim().length > 250) {
            return "La observación no puede superar 250 caracteres."
        }

        return null
    }
}
