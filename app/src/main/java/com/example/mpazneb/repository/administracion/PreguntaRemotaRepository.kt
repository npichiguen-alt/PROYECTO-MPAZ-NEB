package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient

class PreguntaRemotaRepository(
    private val api: MpazApi = RetrofitClient.api
) {

    // ====================================
    // 1. VALIDAR ACTIVIDAD REMOTA
    // ====================================

    private suspend fun verificarPuedeModificar(
        actividadId: Int
    ) {

        require(actividadId > 0) {
            "Selecciona una actividad del servidor."
        }

        val actividad =
            api.obtenerActividadPorId(actividadId)

        check(!actividad.activa) {
            "Primero desactiva la actividad."
        }

        val tieneAsignaciones =
            api.obtenerAsignaciones().any {
                it.actividadId == actividadId
            }

        check(!tieneAsignaciones) {
            "Esta actividad ya fue asignada. " +
            "Crea una actividad nueva."
        }
    }

    // ====================================
    // 2. VALIDAR CONTENIDO
    // ====================================

    private fun validarPregunta(
        pregunta: PreguntaEntity
    ) {

        require(pregunta.enunciado.isNotBlank()) {
            "Ingresa el enunciado."
        }

        val alternativas = listOf(
            pregunta.alternativaA,
            pregunta.alternativaB,
            pregunta.alternativaC,
            pregunta.alternativaD
        )

        require(alternativas.all { it.isNotBlank() }) {
            "Completa las cuatro alternativas."
        }

        require(
            alternativas.map {
                it.trim().lowercase()
            }.distinct().size == 4
        ) {
            "Las alternativas deben ser diferentes."
        }

        require(
            pregunta.respuestaCorrecta in
                listOf("A", "B", "C", "D")
        ) {
            "Selecciona una respuesta correcta."
        }

        require(
            pregunta.retroalimentacion.isNotBlank()
        ) {
            "Ingresa la retroalimentacion."
        }
    }

    // ====================================
    // 3. CONSULTAR PREGUNTAS
    // ====================================

    suspend fun obtenerPreguntasPorActividad(
        actividadId: Int
    ): List<PreguntaEntity> {

        return api.obtenerPreguntasPorActividad(
            actividadId
        )
    }

    // ====================================
    // 4. CREAR PREGUNTA
    // ====================================

    suspend fun crearPregunta(
        pregunta: PreguntaEntity
    ): PreguntaEntity {

        validarPregunta(pregunta)

        verificarPuedeModificar(
            pregunta.actividadId
        )

        return api.crearPregunta(
            pregunta.copy(id = 0)
        )
    }

    // ====================================
    // 5. EDITAR PREGUNTA
    // ====================================

    suspend fun actualizarPregunta(
        pregunta: PreguntaEntity
    ): PreguntaEntity {

        validarPregunta(pregunta)

        verificarPuedeModificar(
            pregunta.actividadId
        )

        val existe =
            api.obtenerPreguntasPorActividad(
                pregunta.actividadId
            ).any {
                it.id == pregunta.id
            }

        check(existe) {
            "La pregunta no pertenece a esta actividad."
        }

        return api.actualizarPregunta(
            pregunta.id,
            pregunta
        )
    }

    // ====================================
    // 6. ELIMINAR PREGUNTA
    // ====================================

    suspend fun eliminarPregunta(
        actividadId: Int,
        preguntaId: Int
    ) {

        verificarPuedeModificar(
            actividadId
        )

        val existe =
            api.obtenerPreguntasPorActividad(
                actividadId
            ).any {
                it.id == preguntaId
            }

        check(existe) {
            "La pregunta no pertenece a esta actividad."
        }

        api.eliminarPregunta(preguntaId)
    }
}