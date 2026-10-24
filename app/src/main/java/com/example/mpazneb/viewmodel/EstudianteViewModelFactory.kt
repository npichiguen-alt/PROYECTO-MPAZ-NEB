
package com.example.mpazneb.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.repository.asignacion.AsignacionRepository
import com.example.mpazneb.repository.estudiante.RoomEstudianteRepository

class EstudianteViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                EstudianteViewModel::class.java
            )
        ) {

            // Obtener la base de datos Room
            val database =
                AsignacionDatabase.obtenerInstancia(context)

            // Repositorio de asignaciones
            val asignacionRepository = AsignacionRepository(
                database.asignacionDao()
            )

            // Repositorio del estudiante
            val estudianteRepository = RoomEstudianteRepository(
                asignacionRepository = asignacionRepository,
                resultadoDao = database.resultadoDao()
            )

            // Crear ViewModel con Room
            @Suppress("UNCHECKED_CAST")
            return EstudianteViewModel(
                estudianteRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
