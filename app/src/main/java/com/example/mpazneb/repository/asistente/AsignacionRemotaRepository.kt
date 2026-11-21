package com.example.mpazneb.repository.asistente

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.AsignacionRequest
import com.example.mpazneb.model.asistente.ObservacionRequest
import com.example.mpazneb.model.asistente.ResultadoAula

import java.text.Normalizer
import java.util.Locale

class AsignacionRemotaRepository(
    private val api: MpazApi = RetrofitClient.api
) {

    private fun normalizar(texto: String): String {
        return Normalizer.normalize(
            texto.trim(),
            Normalizer.Form.NFD
        )
            .replace(Regex("\\p{M}+"), "")
            .lowercase(Locale.ROOT)
    }

    private fun normalizarModulo(modulo: String): String {
        return when (normalizar(modulo)) {
            "matematica", "matematicas" -> "Matemática"
            "lectura" -> "Lectura"
            else -> modulo.trim()
        }
    }

    private fun normalizarDificultad(
        dificultad: String
    ): String {
        return when (normalizar(dificultad)) {
            "facil", "basica", "basico" -> "Básica"

            "medio", "media", "intermedia",
            "intermedio" -> "Media"

            "dificil", "avanzada",
            "avanzado" -> "Avanzada"

            else -> dificultad.trim()
        }
    }

    suspend fun obtenerActividades(): List<ActividadAula> {

        return api.obtenerActividades()
            .filter { it.activa }
            .map { actividad ->

                ActividadAula(
                    id = actividad.id,
                    titulo = actividad.titulo,
                    modulo = normalizarModulo(
                        actividad.modulo
                    ),
                    curso = actividad.curso,
                    dificultad = normalizarDificultad(
                        actividad.dificultad
                    ),
                    descripcion = actividad.descripcion,
                    estado = "Disponible"
                )
            }
    }

    suspend fun crearAsignacion(
        solicitud: AsignacionRequest
    ): AsignacionAula {

        return api.crearAsignacionAsistente(
            solicitud
        )
    }

    suspend fun obtenerAsignaciones(): List<AsignacionAula> {

        return api.obtenerAsignacionesAsistente()
    }

    suspend fun obtenerResultados(
        asignacionId: Int
    ): List<ResultadoAula> {

        return api.obtenerResultadosPorAsignacion(
            asignacionId
        ).map {
            convertirResultado(it)
        }
    }

    suspend fun guardarObservacion(
        resultadoId: Int,
        texto: String
    ): ResultadoAula {

        val resultado = api.actualizarObservacion(
            id = resultadoId,
            solicitud = ObservacionRequest(
                observacion = texto.trim()
            )
        )

        return convertirResultado(resultado)
    }

    private fun convertirResultado(
        resultado: Resultado
    ): ResultadoAula {

        return ResultadoAula(
            id = resultado.id,
            asignacionId = resultado.asignacionId,
            aciertos = resultado.aciertos,
            totalPreguntas = resultado.totalPreguntas,
            intentos = resultado.intentos,
            observacion = resultado.observacion
        )
    }
}