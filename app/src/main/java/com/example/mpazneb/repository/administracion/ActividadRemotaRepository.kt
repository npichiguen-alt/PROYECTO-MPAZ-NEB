
package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest

class ActividadRemotaRepository(
    private val api: MpazApi = RetrofitClient.api
) {

    // 1. CONSULTAR TODAS LAS ACTIVIDADES

    suspend fun obtenerActividades(): List<Actividad> {
        return api.obtenerActividades()
    }

    // 2. CONSULTAR ACTIVIDAD POR ID

    suspend fun obtenerActividadPorId(
        id: Int
    ): Actividad {
        return api.obtenerActividadPorId(id)
    }

    // 3. CREAR ACTIVIDAD

    suspend fun crearActividad(
        actividad: ActividadRequest
    ): Actividad {
        return api.crearActividad(actividad)
    }

    // 4. ACTUALIZAR ACTIVIDAD

    suspend fun actualizarActividad(
        actividad: Actividad
    ): Actividad {
        return api.actualizarActividad(
            id = actividad.id,
            actividad = actividad
        )
    }

    // 5. ACTIVAR O DESACTIVAR ACTIVIDAD

    suspend fun cambiarEstadoActividad(
        id: Int,
        activa: Boolean
    ): Actividad {

        val actividadActual = api.obtenerActividadPorId(id)

        val actividadActualizada = actividadActual.copy(
            activa = activa
        )

        return api.actualizarActividad(
            id = id,
            actividad = actividadActualizada
        )
    }

    // 6. ELIMINAR ACTIVIDAD

    suspend fun eliminarActividad(
        id: Int
    ) {
        api.eliminarActividad(id)
    }
}
