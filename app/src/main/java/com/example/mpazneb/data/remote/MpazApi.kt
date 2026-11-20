
package com.example.mpazneb.data.remote

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MpazApi {

    @GET("api/actividades")
    suspend fun obtenerActividades(): List<Actividad>

    @POST("api/actividades")
    suspend fun crearActividad(
        @Body actividad: ActividadRequest
    ): Actividad
}
