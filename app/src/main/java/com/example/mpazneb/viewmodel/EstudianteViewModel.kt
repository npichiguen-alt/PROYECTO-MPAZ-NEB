
package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Estado del ingreso del estudiante
data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = ""
)

// Estado de la actividad de Lectura
data class LecturaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null
)


data class MatematicaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null
)

class EstudianteViewModel : ViewModel() {

    // INGRESO DEL ESTUDIANTE

    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> = _estado

    fun actualizarCodigo(codigo: String) {
        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = ""
        )
    }

    fun intentarIngresar() {
        val codigo = _estado.value.codigoTemporal.trim()

        val nuevoMensaje = if (codigo.isEmpty()) {
            "Debes ingresar un código."
        } else {
            "Código ingresado. Falta validar la asignación."
        }

        _estado.value = _estado.value.copy(
            mensaje = nuevoMensaje
        )
    }

    // ACTIVIDAD DE LECTURA

    private val _estadoLectura = MutableStateFlow(
        LecturaUiState()
    )

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    fun seleccionarRespuestaLectura(respuesta: String) {
        _estadoLectura.value = _estadoLectura.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    fun comprobarRespuestaLectura() {

        val respuesta =
            _estadoLectura.value.respuestaSeleccionada

        if (respuesta == null) {
            _estadoLectura.value = _estadoLectura.value.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )
            return
        }

        val correcta = respuesta == "Un gato"

        val mensajeResultado = if (correcta) {
            "¡Muy bien! Respuesta correcta."
        } else {
            "Inténtalo nuevamente."
        }

        _estadoLectura.value = _estadoLectura.value.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta
        )
    }
    private val _estadoMatematica = MutableStateFlow(
        MatematicaUiState()
    )

    val estadoMatematica: StateFlow<MatematicaUiState> =
        _estadoMatematica

    fun seleccionarRespuestaMatematica(respuesta: String) {
        _estadoMatematica.value = _estadoMatematica.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    fun comprobarRespuestaMatematica() {
        val respuesta =
            _estadoMatematica.value.respuestaSeleccionada

        if (respuesta == null) {
            _estadoMatematica.value = _estadoMatematica.value.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )
            return
        }

        val correcta = respuesta == "12"

        val mensajeResultado = if (correcta) {
            "¡Muy bien! Respuesta correcta."
        } else {
            "Inténtalo nuevamente."
        }

        _estadoMatematica.value = _estadoMatematica.value.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta
        )
    }
}