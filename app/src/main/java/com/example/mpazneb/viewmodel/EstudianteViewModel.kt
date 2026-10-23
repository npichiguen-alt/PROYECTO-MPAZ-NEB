package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.estudiante.EstudianteRepository
import com.example.mpazneb.repository.estudiante.DemoEstudianteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Estado del ingreso del estudiante
data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = "",
    val ingresoValidado: Boolean = false
)

// Estado de la actividad de Lectura
data class LecturaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

// Estado de la actividad de Matemática
data class MatematicaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

// ====================================
// 2. VIEWMODEL DEL ESTUDIANTE
// ====================================

class EstudianteViewModel(
    private val repositorio: EstudianteRepository =
        DemoEstudianteRepository()
) : ViewModel() {

    // ====================================
    // 3. INGRESO DEL ESTUDIANTE
    // ====================================

    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> =
        _estado

    // Actualiza el código que escribe el estudiante
    fun actualizarCodigo(codigo: String) {
        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = "",
            ingresoValidado = false
        )
    }

    // Verifica el código mediante el Repository
    fun intentarIngresar() {

        val codigo = _estado.value.codigoTemporal.trim()

        if (codigo.isBlank()) {
            _estado.value = _estado.value.copy(
                mensaje = "Debes ingresar un código.",
                ingresoValidado = false
            )
            return
        }

        viewModelScope.launch {

            val asignacion =
                repositorio.buscarAsignacionPorCodigo(codigo)

            if (asignacion != null) {

                _estado.value = _estado.value.copy(
                    mensaje = "Código válido. Asignación encontrada.",
                    ingresoValidado = true
                )

            } else {

                _estado.value = _estado.value.copy(
                    mensaje = "Código no encontrado.",
                    ingresoValidado = false
                )
            }
        }
    }

    // ====================================
    // 4. RESULTADOS DEL ESTUDIANTE
    // ====================================

    private val _resultados = MutableStateFlow<List<Resultado>>(
        emptyList()
    )

    val resultados: StateFlow<List<Resultado>> =
        _resultados

    // ====================================
    // 5. ACTIVIDAD DE LECTURA
    // ====================================

    private val _estadoLectura = MutableStateFlow(
        LecturaUiState()
    )

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    // Guarda la alternativa seleccionada
    fun seleccionarRespuestaLectura(respuesta: String) {

        if (_estadoLectura.value.finalizada) return

        _estadoLectura.value = _estadoLectura.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // Comprueba la respuesta de Lectura
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

        // Aumenta el número de intentos
        val nuevosIntentos = estadoActual.intentos + 1

        // Respuesta correcta del ejercicio de demostración
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

        // Guarda temporalmente el resultado
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
    // 6. ACTIVIDAD DE MATEMÁTICA
    // ====================================

    private val _estadoMatematica = MutableStateFlow(
        MatematicaUiState()
    )

    val estadoMatematica: StateFlow<MatematicaUiState> =
        _estadoMatematica

    // Guarda la alternativa seleccionada
    fun seleccionarRespuestaMatematica(respuesta: String) {

        if (_estadoMatematica.value.finalizada) return

        _estadoMatematica.value = _estadoMatematica.value.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // Comprueba la respuesta de Matemática
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

        // Aumenta el número de intentos
        val nuevosIntentos = estadoActual.intentos + 1

        // Respuesta correcta del ejercicio de demostración
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

        // Guarda temporalmente el resultado
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
