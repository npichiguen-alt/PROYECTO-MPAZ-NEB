package com.example.mpazneb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.asistente.*

import com.example.mpazneb.repository.asistente.AsistenteDataSource


import java.util.Locale
import java.util.UUID

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AsistenteViewModel(
    private val fuente: AsistenteDataSource
) : ViewModel() {

    // ACTIVIDADES DISPONIBLES
    val actividades = fuente.actividades.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // ESTUDIANTES FICTICIOS
    val estudiantes = fuente.estudiantes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // ASIGNACIONES REALIZADAS
    val asignaciones = fuente.asignaciones.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // RESULTADOS DE ACTIVIDADES
    val resultados = fuente.resultados.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // DATOS DEL FORMULARIO
    var formulario by mutableStateOf(FormularioAsignacion())
        private set

    // MENSAJE PARA EL USUARIO
    var mensaje by mutableStateOf("")
        private set

    // INDICA SI SE ESTA GUARDANDO INFORMACION
    var trabajando by mutableStateOf(false)
        private set

    // LIMPIAR MENSAJE
    fun limpiarMensaje() {
        mensaje = ""
    }

    // CAMBIAR MODULO
    fun cambiarModulo(valor: String) {
        formulario = formulario.copy(
            modulo = valor,
            actividadId = 0
        )
    }

    // CAMBIAR CURSO
    fun cambiarCurso(valor: Int) {
        formulario = formulario.copy(
            curso = valor,
            actividadId = 0
        )
    }

    // CAMBIAR DIFICULTAD
    fun cambiarDificultad(valor: String) {
        formulario = formulario.copy(
            dificultad = valor,
            actividadId = 0
        )
    }

    // SELECCIONAR ACTIVIDAD
    fun elegirActividad(id: Int) {
        formulario = formulario.copy(
            actividadId = id
        )
    }

    // SELECCIONAR ESTUDIANTE
    fun elegirEstudiante(alias: String) {
        formulario = formulario.copy(
            estudianteAlias = alias
        )
    }

    // CREAR UNA NUEVA ASIGNACION
    fun asignarActividad() {

        if (trabajando) return

        val datos = formulario

        // VALIDAR EL FORMULARIO
        val error = ValidadorAsignacion.validar(
            datos,
            actividades.value,
            estudiantes.value
        )

        if (error != null) {
            mensaje = error
            return
        }

        // BUSCAR LA ACTIVIDAD SELECCIONADA
        val actividad = actividades.value.first {
            it.id == datos.actividadId
        }

        trabajando = true

        viewModelScope.launch {

            try {

                // GENERAR CODIGO DE DEMOSTRACION
                val codigo = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .take(10)
                    .uppercase(Locale.ROOT)

                // GUARDAR LA ASIGNACION
                fuente.crearAsignacion(
                    AsignacionAula(
                        actividadId = actividad.id,
                        actividadTitulo = actividad.titulo,
                        estudianteAlias = datos.estudianteAlias,
                        codigoTemporal = codigo,
                        asistenteAlias = "ASISTENTE_DEMO",
                        curso = datos.curso
                    )
                )

                mensaje =
                    "Actividad asignada correctamente. Código:  $codigo"

                // LIMPIAR EL FORMULARIO
                formulario = FormularioAsignacion()

            } catch (e: Exception) {

                mensaje =
                    "No se pudo guardar la asignación: ${e.message ?: "intenta otra vez"}"

            } finally {

                trabajando = false

            }
        }
    }

    // GUARDAR OBSERVACION PEDAGOGICA
    fun guardarObservacion(
        resultadoId: Int,
        texto: String
    ) {

        if (trabajando) return

        // VALIDAR LA OBSERVACION
        val error =
            ValidadorAsignacion.validarObservacion(texto)

        if (error != null) {
            mensaje = error
            return
        }

        trabajando = true

        viewModelScope.launch {

            try {

                // GUARDAR LA OBSERVACION
                fuente.guardarObservacion(
                    resultadoId,
                    texto.trim()
                )

                mensaje =
                    "Observación guardada correctamente."

            } catch (e: Exception) {

                mensaje =
                    "No se pudo guardar la observación: ${e.message ?: "error"}"

            } finally {

                trabajando = false

            }
        }
    }
}

// FACTORY PARA CREAR EL VIEWMODEL
class AsistenteViewModelFactory(
    private val fuente: AsistenteDataSource
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                AsistenteViewModel::class.java
            )
        ) {

            @Suppress("UNCHECKED_CAST")
            return AsistenteViewModel(fuente) as T

        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
