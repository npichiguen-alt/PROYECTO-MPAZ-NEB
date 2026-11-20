package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest

class ActividadRemotaRepository {

    private val api = RetrofitClient.api

    // Consultar actividades desde Spring Boot
    suspend fun obtenerActividades(): List<Actividad> {
        return api.obtenerActividades()
    }

    // Crear actividades en PostgreSQL mediante Spring Boot
    suspend fun crearActividad(
        actividad: ActividadRequest
    ): Actividad {
        return api.crearActividad(actividad)
    }
}