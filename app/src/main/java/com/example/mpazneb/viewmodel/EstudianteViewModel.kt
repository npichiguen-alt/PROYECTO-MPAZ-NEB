package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.model.Respuesta
import com.example.mpazneb.repository.estudiante.DemoEstudianteRepository
import com.example.mpazneb.repository.estudiante.EstudianteRepository
import com.example.mpazneb.repository.estudiante.RespuestaRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Máximo de intentos de la demostración
private const val MAX_INTENTOS_DEMO = 3

// ESTADO DEL INGRESO
data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = "",
    val ingresoValidado: Boolean = false,
    val asignacionActiva: Asignacion? = null
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

class EstudianteViewModel(
    private val repositorio: EstudianteRepository =
        DemoEstudianteRepository(),

    private val respuestaRepository: RespuestaRepository? = null
) : ViewModel() {

    // ESTADO DEL INGRESO
    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> =
        _estado

    // RESULTADOS FINALES
    private val _resultados =
        MutableStateFlow<List<Resultado>>(emptyList())

    val resultados: StateFlow<List<Resultado>> =
        _resultados

    // HISTORIAL DE RESPUESTAS
    private val _respuestas =
        MutableStateFlow<List<Respuesta>>(emptyList())

    val respuestas: StateFlow<List<Respuesta>> =
        _respuestas

    // ESTADO DE LECTURA
    private val _estadoLectura = MutableStateFlow(
        LecturaUiState()
    )

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    // ESTADO DE MATEMÁTICA
    private val _estadoMatematica = MutableStateFlow(
        MatematicaUiState()
    )

    val estadoMatematica: StateFlow<MatematicaUiState> =
        _estadoMatematica

    // ====================================
    // 1. ACTUALIZAR CÓDIGO DE INGRESO
    // ====================================

    fun actualizarCodigo(codigo: String) {

        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = "",
            ingresoValidado = false,
            asignacionActiva = null
        )

        // Limpiar sesión anterior
        _estadoLectura.value = LecturaUiState()
        _estadoMatematica.value = MatematicaUiState()

        _resultados.value = emptyList()
        _respuestas.value = emptyList()
    }

    // ====================================
    // 2. VALIDAR INGRESO Y RECUPERAR ROOM
    // ====================================

    fun intentarIngresar() {

        val codigo = _estado.value.codigoTemporal.trim()

        if (codigo.isBlank()) {

            _estado.value = _estado.value.copy(
                mensaje = "Debes ingresar un código.",
                ingresoValidado = false,
                asignacionActiva = null
            )

            return
        }

        viewModelScope.launch {

            try {

                val asignacion =
                    repositorio.buscarAsignacionPorCodigo(
                        codigo
                    )

                // Evitar resultados de otra búsqueda
                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                if (asignacion == null) {

                    _resultados.value = emptyList()
                    _respuestas.value = emptyList()

                    _estado.value = _estado.value.copy(
                        mensaje = "Código no encontrado.",
                        ingresoValidado = false,
                        asignacionActiva = null
                    )

                    return@launch
                }

                // Recuperar resultados guardados
                val resultadosGuardados =
                    repositorio.obtenerResultados(
                        asignacion.id
                    )

                // Recuperar respuestas guardadas
                val respuestasGuardadas =
                    respuestaRepository
                        ?.obtenerRespuestasParaMostrar(
                            asignacion.id
                        )
                        ?: emptyList()

                // Comprobar que siga siendo el mismo código
                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                _resultados.value = resultadosGuardados
                _respuestas.value = respuestasGuardadas

                // Recuperar el último intento de Lectura
                val ultimoIntentoLectura =
                    respuestasGuardadas
                        .filter {
                            it.actividadId == 1 &&
                                    it.preguntaId == 1
                        }
                        .maxOfOrNull {
                            it.numeroIntento
                        } ?: 0

                // Recuperar el último intento de Matemática
                val ultimoIntentoMatematica =
                    respuestasGuardadas
                        .filter {
                            it.actividadId == 2 &&
                                    it.preguntaId == 1
                        }
                        .maxOfOrNull {
                            it.numeroIntento
                        } ?: 0

                // Compatibilidad con resultados anteriores
                val intentosResultadoAnterior =
                    resultadosGuardados
                        .maxOfOrNull {
                            it.intentos
                        } ?: 0

                // Resultado final más reciente, si existe
                val resultadoFinal =
                    resultadosGuardados.firstOrNull()

                // Saber si respondió correctamente
                val acerto =
                    resultadoFinal?.aciertos?.let {
                        it > 0
                    }

                // ====================================
                // RESTAURAR LECTURA
                // ====================================

                val intentosLectura =
                    if (asignacion.actividadId == 1) {

                        maxOf(
                            ultimoIntentoLectura,
                            intentosResultadoAnterior
                        )

                    } else {
                        0
                    }

                val lecturaAgotada =
                    asignacion.actividadId == 1 &&
                            intentosLectura >= MAX_INTENTOS_DEMO &&
                            acerto != true

                _estadoLectura.value = LecturaUiState(
                    intentos = intentosLectura,

                    esCorrecta =
                        if (asignacion.actividadId == 1) {
                            acerto
                        } else {
                            null
                        },

                    finalizada =
                        asignacion.actividadId == 1 &&
                                (
                                        resultadoFinal != null ||
                                                lecturaAgotada
                                        ),

                    mensaje =
                        if (lecturaAgotada) {
                            "Has agotado los 3 intentos permitidos."
                        } else {
                            ""
                        }
                )

                // ====================================
                // RESTAURAR MATEMÁTICA
                // ====================================

                val intentosMatematica =
                    if (asignacion.actividadId == 2) {

                        maxOf(
                            ultimoIntentoMatematica,
                            intentosResultadoAnterior
                        )

                    } else {
                        0
                    }

                val matematicaAgotada =
                    asignacion.actividadId == 2 &&
                            intentosMatematica >= MAX_INTENTOS_DEMO &&
                            acerto != true

                _estadoMatematica.value = MatematicaUiState(
                    intentos = intentosMatematica,

                    esCorrecta =
                        if (asignacion.actividadId == 2) {
                            acerto
                        } else {
                            null
                        },

                    finalizada =
                        asignacion.actividadId == 2 &&
                                (
                                        resultadoFinal != null ||
                                                matematicaAgotada
                                        ),

                    mensaje =
                        if (matematicaAgotada) {
                            "Has agotado los 3 intentos permitidos."
                        } else {
                            ""
                        }
                )

                // Ingreso correcto
                _estado.value = _estado.value.copy(
                    mensaje = "Código válido. Asignación encontrada.",
                    ingresoValidado = true,
                    asignacionActiva = asignacion
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (
                    _estado.value.codigoTemporal.trim() == codigo
                ) {

                    _estado.value = _estado.value.copy(
                        mensaje = "No se pudo verificar el código.",
                        ingresoValidado = false,
                        asignacionActiva = null
                    )
                }
            }
        }
    }

    // ====================================
    // 3. SELECCIONAR RESPUESTA DE LECTURA
    // ====================================

    fun seleccionarRespuestaLectura(respuesta: String) {

        val actual = _estadoLectura.value

        if (actual.finalizada) return

        if (actual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoLectura.value = actual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        _estadoLectura.value = actual.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // ====================================
    // 4. COMPROBAR RESPUESTA DE LECTURA
    // ====================================

    fun comprobarRespuestaLectura() {

        val asignacion =
            _estado.value.asignacionActiva ?: return

        // ID 1 corresponde a Lectura en la demostración
        if (asignacion.actividadId != 1) return

        val actual = _estadoLectura.value

        if (actual.finalizada) return

        if (actual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoLectura.value = actual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        val respuesta = actual.respuestaSeleccionada

        if (respuesta == null) {

            _estadoLectura.value = actual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )

            return
        }

        val nuevosIntentos = actual.intentos + 1

        // Respuesta correcta de la actividad demostrativa
        val correcta = respuesta == "Un gato"

        // Termina al acertar o agotar los intentos
        val terminaActividad =
            correcta || nuevosIntentos >= MAX_INTENTOS_DEMO

        val mensajeResultado = when {

            correcta ->
                "¡Muy bien! Respuesta correcta."

            nuevosIntentos >= MAX_INTENTOS_DEMO ->
                "Respuesta incorrecta. Has agotado los 3 intentos permitidos."

            else ->
                "Respuesta incorrecta. Inténtalo nuevamente."
        }

        _estadoLectura.value = actual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = terminaActividad
        )

        viewModelScope.launch {

            try {

                // Guardar cada respuesta en Room
                if (respuestaRepository != null) {

                    respuestaRepository.guardarRespuesta(
                        asignacionId = asignacion.id,
                        actividadId = 1,
                        preguntaId = 1,
                        respuestaSeleccionada = respuesta,
                        esCorrecta = correcta,
                        numeroIntento = nuevosIntentos
                    )

                    val respuestasGuardadas =
                        respuestaRepository
                            .obtenerRespuestasParaMostrar(
                                asignacion.id
                            )

                    if (
                        _estado.value.asignacionActiva?.id ==
                        asignacion.id
                    ) {
                        _respuestas.value = respuestasGuardadas
                    }
                }

                // Guardar resultado final al acertar
                // o al agotar tres intentos
                if (terminaActividad) {

                    val nuevoResultado = Resultado(
                        id = 0,
                        asignacionId = asignacion.id,
                        aciertos = if (correcta) 1 else 0,
                        totalPreguntas = 1,
                        intentos = nuevosIntentos,
                        observacion =
                            "Actividad de Lectura (demostración)"
                    )

                    repositorio.guardarResultado(
                        nuevoResultado
                    )

                    val resultadosGuardados =
                        repositorio.obtenerResultados(
                            asignacion.id
                        )

                    if (
                        _estado.value.asignacionActiva?.id ==
                        asignacion.id
                    ) {
                        _resultados.value = resultadosGuardados
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (
                    _estado.value.asignacionActiva?.id ==
                    asignacion.id
                ) {

                    _estadoLectura.value =
                        _estadoLectura.value.copy(
                            mensaje =
                                "No se pudo guardar el intento o resultado."
                        )
                }
            }
        }
    }

    // ====================================
    // 5. SELECCIONAR RESPUESTA DE MATEMÁTICA
    // ====================================

    fun seleccionarRespuestaMatematica(respuesta: String) {

        val actual = _estadoMatematica.value

        if (actual.finalizada) return

        if (actual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoMatematica.value = actual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        _estadoMatematica.value = actual.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // ====================================
    // 6. COMPROBAR RESPUESTA DE MATEMÁTICA
    // ====================================

    fun comprobarRespuestaMatematica() {

        val asignacion =
            _estado.value.asignacionActiva ?: return

        // ID 2 corresponde a Matemática en la demostración
        if (asignacion.actividadId != 2) return

        val actual = _estadoMatematica.value

        if (actual.finalizada) return

        if (actual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoMatematica.value = actual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        val respuesta = actual.respuestaSeleccionada

        if (respuesta == null) {

            _estadoMatematica.value = actual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )

            return
        }

        val nuevosIntentos = actual.intentos + 1

        // Respuesta correcta de la actividad demostrativa
        val correcta = respuesta == "12"

        val terminaActividad =
            correcta || nuevosIntentos >= MAX_INTENTOS_DEMO

        val mensajeResultado = when {

            correcta ->
                "¡Muy bien! Respuesta correcta."

            nuevosIntentos >= MAX_INTENTOS_DEMO ->
                "Respuesta incorrecta. Has agotado los 3 intentos permitidos."

            else ->
                "Respuesta incorrecta. Inténtalo nuevamente."
        }

        _estadoMatematica.value = actual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = terminaActividad
        )

        viewModelScope.launch {

            try {

                // Guardar respuesta individual en Room
                if (respuestaRepository != null) {

                    respuestaRepository.guardarRespuesta(
                        asignacionId = asignacion.id,
                        actividadId = 2,
                        preguntaId = 1,
                        respuestaSeleccionada = respuesta,
                        esCorrecta = correcta,
                        numeroIntento = nuevosIntentos
                    )

                    val respuestasGuardadas =
                        respuestaRepository
                            .obtenerRespuestasParaMostrar(
                                asignacion.id
                            )

                    if (
                        _estado.value.asignacionActiva?.id ==
                        asignacion.id
                    ) {
                        _respuestas.value = respuestasGuardadas
                    }
                }

                // Guardar resultado final con o sin acierto
                if (terminaActividad) {

                    val nuevoResultado = Resultado(
                        id = 0,
                        asignacionId = asignacion.id,
                        aciertos = if (correcta) 1 else 0,
                        totalPreguntas = 1,
                        intentos = nuevosIntentos,
                        observacion =
                            "Actividad de Matemática (demostración)"
                    )

                    repositorio.guardarResultado(
                        nuevoResultado
                    )

                    val resultadosGuardados =
                        repositorio.obtenerResultados(
                            asignacion.id
                        )

                    if (
                        _estado.value.asignacionActiva?.id ==
                        asignacion.id
                    ) {
                        _resultados.value = resultadosGuardados
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                if (
                    _estado.value.asignacionActiva?.id ==
                    asignacion.id
                ) {

                    _estadoMatematica.value =
                        _estadoMatematica.value.copy(
                            mensaje =
                                "No se pudo guardar el intento o resultado."
                        )
                }
            }
        }
    }
}