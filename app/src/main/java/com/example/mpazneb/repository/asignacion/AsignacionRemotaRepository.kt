package com.example.mpazneb.repository.asignacion

import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.AsignacionRequest
import com.example.mpazneb.model.AsignacionRemota

class AsignacionRemotaRepository {

    private val api = RetrofitClient.api

    // CONSULTAR TODAS LAS ASIGNACIONES
    suspend fun obtenerAsignaciones(): List<AsignacionRemota> {
        return api.obtenerAsignaciones()
    }

    // CONSULTAR ASIGNACIONES DE UN ESTUDIANTE
    suspend fun obtenerAsignacionesPorEstudiante(
        alias: String
    ): List<AsignacionRemota> {
        return api.obtenerAsignacionesPorEstudiante(alias)
    }

    // BUSCAR UNA ASIGNACION POR CODIGO TEMPORAL
    suspend fun obtenerAsignacionPorCodigo(
        codigo: String
    ): AsignacionRemota {
        return api.obtenerAsignacionPorCodigo(codigo)
    }

    // CREAR UNA ASIGNACION EN POSTGRESQL RDS
    suspend fun crearAsignacion(
        solicitud: AsignacionRequest
    ): AsignacionRemota {
        return api.crearAsignacion(solicitud)
    }
}
