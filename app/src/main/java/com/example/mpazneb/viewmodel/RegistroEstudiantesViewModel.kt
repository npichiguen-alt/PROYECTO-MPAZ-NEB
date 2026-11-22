package com.example.mpazneb.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.EstudianteRemoto
import com.example.mpazneb.repository.administracion.RegistroEstudiantesRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import retrofit2.HttpException
import java.io.IOException

class RegistroEstudiantesViewModel(
    private val repositorio: RegistroEstudiantesRepository =
        RegistroEstudiantesRepository()
) : ViewModel() {

    var estudiantes by mutableStateOf<List<EstudianteRemoto>>(emptyList())
        private set

    var cargando by mutableStateOf(false)
        private set

    var guardando by mutableStateOf(false)
        private set

    var consultaExitosa by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf("")
        private set

    var hayError by mutableStateOf(false)
        private set

    fun cargarEstudiantes() {

        if (cargando || guardando) return

        cargando = true
        hayError = false

        mensaje = "Consultando estudiantes..."

        viewModelScope.launch {

            try {

                estudiantes = repositorio.obtenerEstudiantes()

                consultaExitosa = true

                mensaje = "Estudiantes actualizados."

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                consultaExitosa = false
                mostrarError(error)

            } finally {

                cargando = false
            }
        }
    }

    fun registrarEstudiante(
        alias: String,
        alRegistrar: () -> Unit
    ) {

        if (guardando || cargando) return

        guardando = true
        hayError = false

        mensaje = "Registrando estudiante..."

        viewModelScope.launch {

            try {

                val nuevo = repositorio.registrarEstudiante(alias)

                estudiantes = (estudiantes + nuevo)
                    .distinctBy { it.id }
                    .sortedBy { it.alias.lowercase() }

                alRegistrar()

                mensaje = "Estudiante registrado correctamente."

                try {

                    estudiantes = repositorio.obtenerEstudiantes()
                    consultaExitosa = true

                } catch (error: CancellationException) {

                    throw error

                } catch (error: Exception) {

                    consultaExitosa = false
                    hayError = true

                    mensaje = "Estudiante registrado, pero no se pudo actualizar el listado."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mostrarError(error)

            } finally {

                guardando = false
            }
        }
    }

    private fun mostrarError(error: Exception) {

        hayError = true

        mensaje = when (error) {

            is IllegalArgumentException ->
                error.message ?: "Alias invalido."

            is HttpException -> when (error.code()) {

                400 -> "El alias no es valido."

                409 -> "El alias ya existe o no se pudo registrar."

                else -> "Error del servidor: ${error.code()}."
            }

            is IOException ->
                "No se pudo conectar con el servidor."

            else ->
                "No se pudo completar la operacion."
        }
    }
}