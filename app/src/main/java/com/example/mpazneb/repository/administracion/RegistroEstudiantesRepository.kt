package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient

import com.example.mpazneb.model.EstudianteRemoto
import com.example.mpazneb.model.RegistroEstudianteRequest

class RegistroEstudiantesRepository(
    private val api: MpazApi = RetrofitClient.api
) {

    suspend fun obtenerEstudiantes(): List<EstudianteRemoto> {

        return api.obtenerEstudiantes()
            .sortedBy { it.alias.lowercase() }
    }

    suspend fun obtenerEstudiantesActivos(): List<EstudianteRemoto> {

        return api.obtenerEstudiantesActivos()
            .sortedBy { it.alias.lowercase() }
    }

    suspend fun obtenerEstudiantePorId(
        id: Int
    ): EstudianteRemoto {

        return api.obtenerEstudiantePorId(id)
    }

    suspend fun registrarEstudiante(
        alias: String
    ): EstudianteRemoto {

        val aliasNormalizado = alias
            .trim()
            .replace(Regex("\\s+"), " ")

        require(aliasNormalizado.length in 2..60) {
            "El alias debe tener entre 2 y 60 caracteres."
        }

        val solicitud = RegistroEstudianteRequest(
            alias = aliasNormalizado
        )

        return api.registrarEstudiante(solicitud)
    }
}