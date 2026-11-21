
package com.example.mpazneb.data.remote

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest
import com.example.mpazneb.model.AsignacionRequest
import com.example.mpazneb.model.AsignacionRemota
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.model.RespuestaRequest
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.data.local.PreguntaEntity

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

    @GET("api/actividades/{id}")
    suspend fun obtenerActividadPorId(
        @Path("id") id: Int
    ): Actividad

    @GET("api/preguntas/actividad/{actividadId}")
    suspend fun obtenerPreguntasPorActividad(
        @Path("actividadId") actividadId: Int
    ): List<PreguntaEntity>


    // RESPUESTAS DEL MÓDULO ESTUDIANTE

    @POST("api/respuestas")
    suspend fun registrarRespuesta(
        @Body solicitud: RespuestaRequest
    ): Respuesta

    @GET("api/respuestas/asignacion/{asignacionId}")
    suspend fun obtenerRespuestasPorAsignacion(
        @Path("asignacionId") asignacionId: Int
    ): List<Respuesta>


    // RESULTADOS DEL MÓDULO ESTUDIANTE

    @GET("api/resultados/asignacion/{asignacionId}")
    suspend fun obtenerResultadosPorAsignacion(
        @Path("asignacionId") asignacionId: Int
    ): List<Resultado>

    @POST("api/resultados/asignacion/{asignacionId}/calcular")
    suspend fun calcularResultado(
        @Path("asignacionId") asignacionId: Int
    ): Resultado


    // ASIGNACIONES DEL MÓDULO ASISTENTE

    @POST("api/asignaciones")
    suspend fun crearAsignacionAsistente(
        @Body asignacion: AsignacionAsistenteRequest
    ): AsignacionAula

    @GET("api/asignaciones")
    suspend fun obtenerAsignacionesAsistente(): List<AsignacionAula>
}
