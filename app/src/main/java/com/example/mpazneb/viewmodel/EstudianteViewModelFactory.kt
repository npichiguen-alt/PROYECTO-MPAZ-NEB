
package com.example.mpazneb.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.repository.asignacion.AsignacionRepository
import com.example.mpazneb.repository.estudiante.RoomEstudianteRepository
import com.example.mpazneb.repository.estudiante.RespuestaRepository

class EstudianteViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                EstudianteViewModel::class.java
            )
        ) {

            // ====================================
            // 1. OBTENER LA BASE DE DATOS ROOM
            // ====================================

            val database =
                AsignacionDatabase.obtenerInstancia(context)

            // ====================================
            // 2. REPOSITORIO DE ASIGNACIONES
            // ====================================

            val asignacionRepository = AsignacionRepository(
                database.asignacionDao()
            )

            // ====================================
            // 3. REPOSITORIO DEL ESTUDIANTE
            // ====================================

            val estudianteRepository = RoomEstudianteRepository(
                asignacionRepository = asignacionRepository,
                resultadoDao = database.resultadoDao()
            )

            // ====================================
            // 4. REPOSITORIO DE RESPUESTAS
            // ====================================

            val respuestaRepository = RespuestaRepository(
                database.respuestaDao()
            )

            // ====================================
            // 5. CREAR VIEWMODEL CON ROOM
            // ====================================

            @Suppress("UNCHECKED_CAST")
            return EstudianteViewModel(
                repositorio = estudianteRepository,
                respuestaRepository = respuestaRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
