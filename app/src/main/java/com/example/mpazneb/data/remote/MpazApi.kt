
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
import com.example.mpazneb.model.asistente.ObservacionRequest

import com.example.mpazneb.model.EstudianteRemoto
import com.example.mpazneb.model.RegistroEstudianteRequest

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.Path

interface MpazApi {

    // ==========================================
    // ADMINISTRACION - ACTIVIDADES
    // ==========================================

    @GET("api/actividades")
    suspend fun obtenerActividades(): List<Actividad>

    @GET("api/actividades/{id}")
    suspend fun obtenerActividadPorId(
        @Path("id") id: Int
    ): Actividad

    @POST("api/actividades")
    suspend fun crearActividad(
        @Body actividad: ActividadRequest
    ): Actividad

    @PUT("api/actividades/{id}")
    suspend fun actualizarActividad(
        @Path("id") id: Int,
        @Body actividad: Actividad
    ): Actividad

    @DELETE("api/actividades/{id}")
    suspend fun eliminarActividad(
        @Path("id") id: Int
    )

    // ==========================================
    // ADMINISTRACION - PREGUNTAS
    // ==========================================

    @GET("api/preguntas/actividad/{actividadId}")
    suspend fun obtenerPreguntasPorActividad(
        @Path("actividadId") actividadId: Int
    ): List<PreguntaEntity>

    @POST("api/preguntas")
    suspend fun crearPregunta(
        @Body pregunta: PreguntaEntity
    ): PreguntaEntity

    @PUT("api/preguntas/{id}")
    suspend fun actualizarPregunta(
        @Path("id") id: Int,
        @Body pregunta: PreguntaEntity
    ): PreguntaEntity

    @DELETE("api/preguntas/{id}")
    suspend fun eliminarPregunta(
        @Path("id") id: Int
    )


    // ==========================================
    // ASIGNACIONES
    // ==========================================

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

    // ==========================================
    // ESTUDIANTE
    // ==========================================

    @POST("api/respuestas")
    suspend fun registrarRespuesta(
        @Body solicitud: RespuestaRequest
    ): Respuesta

    @GET("api/respuestas/asignacion/{asignacionId}")
    suspend fun obtenerRespuestasPorAsignacion(
        @Path("asignacionId") asignacionId: Int
    ): List<Respuesta>

    @GET("api/resultados/asignacion/{asignacionId}")
    suspend fun obtenerResultadosPorAsignacion(
        @Path("asignacionId") asignacionId: Int
    ): List<Resultado>

    @POST("api/resultados/asignacion/{asignacionId}/calcular")
    suspend fun calcularResultado(
        @Path("asignacionId") asignacionId: Int
    ): Resultado

    // ==========================================
    // ASISTENTE
    // ==========================================

    @POST("api/asignaciones")
    suspend fun crearAsignacionAsistente(
        @Body asignacion: AsignacionAsistenteRequest
    ): AsignacionAula

    @GET("api/asignaciones")
    suspend fun obtenerAsignacionesAsistente(): List<AsignacionAula>

    // ==========================================
    // OBSERVACIONES PEDAGOGICAS
    // ==========================================

    @PATCH("api/resultados/{id}/observacion")
    suspend fun actualizarObservacion(
        @Path("id") id: Int,
        @Body solicitud: ObservacionRequest
    ): Resultado

    // ==========================================
    // ADMINISTRACION - ESTUDIANTES REMOTOS
    // ==========================================

    @GET("api/estudiantes")
    suspend fun obtenerEstudiantes(): List<EstudianteRemoto>

    @GET("api/estudiantes/activos")
    suspend fun obtenerEstudiantesActivos(): List<EstudianteRemoto>

    @GET("api/estudiantes/{id}")
    suspend fun obtenerEstudiantePorId(
        @Path("id") id: Int
    ): EstudianteRemoto

    @POST("api/estudiantes")
    suspend fun registrarEstudiante(
        @Body solicitud: RegistroEstudianteRequest
    ): EstudianteRemoto

}
