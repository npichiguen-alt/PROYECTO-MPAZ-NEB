
package com.example.mpazneb.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.AsignacionDatabase

import com.example.mpazneb.repository.asignacion.AsignacionRepository
import com.example.mpazneb.repository.estudiante.ContenidoEstudianteRepository
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

            // Base de datos de asignaciones,
            // resultados y respuestas.
            val database =
                AsignacionDatabase.obtenerInstancia(context)

            // Base de datos de actividades
            // y preguntas creadas por Administración.
            val actividadDatabase =
                ActividadDatabase.obtenerInstancia(context)

            // Repositorio de asignaciones.
            val asignacionRepository = AsignacionRepository(
                database.asignacionDao()
            )

            // Repositorio principal del Estudiante.
            val estudianteRepository = RoomEstudianteRepository(
                asignacionRepository = asignacionRepository,
                resultadoDao = database.resultadoDao()
            )

            // Repositorio de respuestas.
            val respuestaRepository = RespuestaRepository(
                database.respuestaDao()
            )

            // NUEVO: repositorio de contenido pedagógico.
            val contenidoRepository = ContenidoEstudianteRepository(
                actividadDatabase
            )

            @Suppress("UNCHECKED_CAST")
            return EstudianteViewModel(
                repositorio = estudianteRepository,
                respuestaRepository = respuestaRepository,
                contenidoRepository = contenidoRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
