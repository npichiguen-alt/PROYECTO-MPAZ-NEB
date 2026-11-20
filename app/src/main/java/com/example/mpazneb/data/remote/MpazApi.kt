
package com.example.mpazneb.data.remote

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest
import com.example.mpazneb.model.AsignacionRequest
import com.example.mpazneb.model.AsignacionRemota

import com.example.mpazneb.model.asistente.AsignacionRequest as AsignacionAsistenteRequest
import com.example.mpazneb.model.asistente.AsignacionAula

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MpazApi {

    // ACTIVIDADES DE ADMINISTRACIÓN

    @GET("api/actividades")
    suspend fun obtenerActividades(): List<Actividad>

    @POST("api/actividades")
    suspend fun crearActividad(
        @Body actividad: ActividadRequest
    ): Actividad


    // ASIGNACIONES DE MATÍAS

    @GET("api/asignaciones")
    suspend fun obtenerAsignaciones(): List<AsignacionRemota>

    @GET("api/asignaciones/codigo/{codigo}")
    suspend fun obtenerAsignacionPorCodigo(
        @Path("codigo") codigo: String
    ): AsignacionRemota

    @GET("api/asignaciones/estudiante/{alias}")
    suspend fun obtenerAsignacionesPorEstudiante(
        @Path("alias") alias: String
    ): List<AsignacionRemota>

    @POST("api/asignaciones")
    suspend fun crearAsignacion(
        @Body asignacion: AsignacionRequest
    ): AsignacionRemota


    // ASIGNACIONES DEL MÓDULO ASISTENTE

    @POST("api/asignaciones")
    suspend fun crearAsignacionAsistente(
        @Body asignacion: AsignacionAsistenteRequest
    ): AsignacionAula

    @GET("api/asignaciones")
    suspend fun obtenerAsignacionesAsistente(): List<AsignacionAula>
}
