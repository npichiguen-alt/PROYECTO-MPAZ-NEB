
package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mpazneb.model.Resultado
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// ESTADO DEL INGRESO
data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = ""
)

// ESTADO DE LECTURA
data class LecturaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

// ESTADO DE MATEMÁTICA
data class MatematicaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

class EstudianteViewModel : ViewModel() {

    // ====================================
    // 1. INGRESO DEL ESTUDIANTE
    // ====================================

    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> =
        _estado

    fun actualizarCodigo(codigo: String) {
        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = ""
        )
    }

    fun intentarIngresar() {
        val codigo = _estado.value.codigoTemporal.trim()

        val mensaje = if (codigo.isEmpty()) {
            "Debes ingresar un código."
        } else {
            "Código ingresado. Falta validar la asignación."
        }

        _estado.value = _estado.value.copy(
            mensaje = mensaje
        )
    }

    // ====================================
    // 2. RESULTADOS DEL ESTUDIANTE
    // ====================================

    private val _resultados = MutableStateFlow<List<Resultado>>(
        emptyList()
    )

    val resultados: StateFlow<List<Resultado>> =
        _resultados

    // ====================================
    // 3. ACTIVIDAD DE LECTURA
    // ====================================

    private val _estadoLectura = MutableStateFlow(
        LecturaUiState()
    )

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    fun seleccionarRespuestaLectura(respuesta: String) {

        if (_estadoLectura.value.finalizada) return

        _estadoLectura.value = _estadoLectura.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    fun comprobarRespuestaLectura() {

        val estadoActual = _estadoLectura.value

        if (estadoActual.finalizada) return

        val respuesta = estadoActual.respuestaSeleccionada

        if (respuesta == null) {
            _estadoLectura.value = estadoActual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )
            return
        }

        // Sumamos un intento
        val nuevosIntentos = estadoActual.intentos + 1

        val correcta = respuesta == "Un gato"

        val mensajeResultado = if (correcta) {
            "¡Muy bien! Respuesta correcta."
        } else {
            "Inténtalo nuevamente."
        }

        _estadoLectura.value = estadoActual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = correcta
        )

        // Guardamos el resultado al completar la actividad
        if (correcta) {

            val nuevoResultado = Resultado(
                id = 1,
                asignacionId = 1,
                aciertos = 1,
                totalPreguntas = 1,
                intentos = nuevosIntentos,
                observacion = "Actividad de Lectura (demostración)"
            )

            _resultados.value =
                _resultados.value + nuevoResultado
        }
    }

    // ====================================
    // 4. ACTIVIDAD DE MATEMÁTICA
    // ====================================

    private val _estadoMatematica = MutableStateFlow(
        MatematicaUiState()
    )

    val estadoMatematica: StateFlow<MatematicaUiState> =
        _estadoMatematica

    fun seleccionarRespuestaMatematica(respuesta: String) {

        if (_estadoMatematica.value.finalizada) return

        _estadoMatematica.value = _estadoMatematica.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    fun comprobarRespuestaMatematica() {

        val estadoActual = _estadoMatematica.value

        if (estadoActual.finalizada) return

        val respuesta = estadoActual.respuestaSeleccionada

        if (respuesta == null) {
            _estadoMatematica.value = estadoActual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )
            return
        }

        // Sumamos un intento
        val nuevosIntentos = estadoActual.intentos + 1

        val correcta = respuesta == "12"

        val mensajeResultado = if (correcta) {
            "¡Muy bien! Respuesta correcta."
        } else {
            "Inténtalo nuevamente."
        }

        _estadoMatematica.value = estadoActual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = correcta
        )

        // Guardamos el resultado al completar la actividad
        if (correcta) {

            val nuevoResultado = Resultado(
                id = 2,
                asignacionId = 2,
                aciertos = 1,
                totalPreguntas = 1,
                intentos = nuevosIntentos,
                observacion = "Actividad de Matemática (demostración)"
            )

            _resultados.value =
                _resultados.value + nuevoResultado
        }
    }

}
