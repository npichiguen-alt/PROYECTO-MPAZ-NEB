
package com.example.mpazneb.repository.asistente

import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.asistente.AsignacionAula
import com.example.mpazneb.model.asistente.AsignacionRequest

class AsignacionRemotaRepository {

    private val api = RetrofitClient.api

    suspend fun crearAsignacion(
        solicitud: AsignacionRequest
    ): AsignacionAula {
        return api.crearAsignacionAsistente(solicitud)
    }

    suspend fun obtenerAsignaciones(): List<AsignacionAula> {
        return api.obtenerAsignacionesAsistente()
    }
}
