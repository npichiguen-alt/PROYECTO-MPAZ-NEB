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

// ====================================
// 1. CONFIGURACIÓN DE LA DEMOSTRACIÓN
// ====================================

// Máximo de intentos permitidos por actividad.
// Es un valor provisional para las pruebas.
private const val MAX_INTENTOS_DEMO = 3

// ====================================
// 2. ESTADOS DEL ESTUDIANTE
// ====================================

data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = "",
    val ingresoValidado: Boolean = false,
    val asignacionActiva: Asignacion? = null
)

data class LecturaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

data class MatematicaUiState(
    val respuestaSeleccionada: String? = null,
    val mensaje: String = "",
    val esCorrecta: Boolean? = null,
    val intentos: Int = 0,
    val finalizada: Boolean = false
)

// ====================================
// 3. VIEWMODEL DEL ESTUDIANTE
// ====================================

class EstudianteViewModel(
    private val repositorio: EstudianteRepository =
        DemoEstudianteRepository(),

    private val respuestaRepository: RespuestaRepository? = null
) : ViewModel() {

    // ====================================
    // 4. ESTADO DEL INGRESO
    // ====================================

    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> =
        _estado

    // ====================================
    // 5. RESULTADOS FINALES
    // ====================================

    private val _resultados = MutableStateFlow<List<Resultado>>(
        emptyList()
    )

    val resultados: StateFlow<List<Resultado>> =
        _resultados

    // ====================================
    // 6. HISTORIAL DE RESPUESTAS
    // ====================================

    private val _respuestas = MutableStateFlow<List<Respuesta>>(
        emptyList()
    )

    val respuestas: StateFlow<List<Respuesta>> =
        _respuestas

    // ====================================
    // 7. ESTADO DE LECTURA
    // ====================================

    private val _estadoLectura = MutableStateFlow(
        LecturaUiState()
    )

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    // ====================================
    // 8. ESTADO DE MATEMÁTICA
    // ====================================

    private val _estadoMatematica = MutableStateFlow(
        MatematicaUiState()
    )

    val estadoMatematica: StateFlow<MatematicaUiState> =
        _estadoMatematica

    // ====================================
    // 9. ACTUALIZAR CÓDIGO
    // ====================================

    fun actualizarCodigo(codigo: String) {

        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = "",
            ingresoValidado = false,
            asignacionActiva = null
        )

        // Limpiar la sesión anterior
        _estadoLectura.value = LecturaUiState()
        _estadoMatematica.value = MatematicaUiState()

        _resultados.value = emptyList()
        _respuestas.value = emptyList()
    }

    // ====================================
    // 10. VALIDAR INGRESO
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

                // Buscar la asignación usando el código
                val asignacion =
                    repositorio.buscarAsignacionPorCodigo(codigo)

                // Evitar utilizar una búsqueda anterior
                if (_estado.value.codigoTemporal.trim() != codigo) {
                    return@launch
                }

                if (asignacion != null) {

                    // ====================================
                    // RECUPERAR RESULTADOS FINALES
                    // ====================================

                    val resultadosGuardados =
                        repositorio.obtenerResultados(
                            asignacion.id
                        )

                    // ====================================
                    // RECUPERAR RESPUESTAS INDIVIDUALES
                    // ====================================

                    val respuestasGuardadas =
                        respuestaRepository
                            ?.obtenerRespuestasParaMostrar(
                                asignacion.id
                            )
                            ?: emptyList()

                    // Evitar mezclar datos de otra sesión
                    if (
                        _estado.value.codigoTemporal.trim() != codigo
                    ) {
                        return@launch
                    }

                    _resultados.value = resultadosGuardados
                    _respuestas.value = respuestasGuardadas

                    // ====================================
                    // RECUPERAR INTENTOS DE LECTURA
                    // ====================================

                    val ultimoIntentoLectura =
                        respuestasGuardadas
                            .filter {
                                it.actividadId == 1 &&
                                        it.preguntaId == 1
                            }
                            .maxOfOrNull {
                                it.numeroIntento
                            } ?: 0

                    // ====================================
                    // RECUPERAR INTENTOS DE MATEMÁTICA
                    // ====================================

                    val ultimoIntentoMatematica =
                        respuestasGuardadas
                            .filter {
                                it.actividadId == 2 &&
                                        it.preguntaId == 1
                            }
                            .maxOfOrNull {
                                it.numeroIntento
                            } ?: 0

                    // Recuperar intentos de resultados
                    // antiguos que no tengan respuestas
                    // individuales registradas
                    val intentosResultadoAnterior =
                        resultadosGuardados
                            .maxOfOrNull {
                                it.intentos
                            } ?: 0

                    // ====================================
                    // RESTAURAR PROGRESO DE LECTURA
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

                    _estadoLectura.value = LecturaUiState(
                        intentos = intentosLectura,
                        finalizada =
                            asignacion.actividadId == 1 &&
                                    resultadosGuardados.isNotEmpty(),
                        mensaje = if (
                            asignacion.actividadId == 1 &&
                            resultadosGuardados.isEmpty() &&
                            intentosLectura >= MAX_INTENTOS_DEMO
                        ) {
                            "Has agotado los 3 intentos permitidos."
                        } else {
                            ""
                        }
                    )

                    // ====================================
                    // RESTAURAR PROGRESO DE MATEMÁTICA
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

                    _estadoMatematica.value = MatematicaUiState(
                        intentos = intentosMatematica,
                        finalizada =
                            asignacion.actividadId == 2 &&
                                    resultadosGuardados.isNotEmpty(),
                        mensaje = if (
                            asignacion.actividadId == 2 &&
                            resultadosGuardados.isEmpty() &&
                            intentosMatematica >= MAX_INTENTOS_DEMO
                        ) {
                            "Has agotado los 3 intentos permitidos."
                        } else {
                            ""
                        }
                    )

                    // ====================================
                    // PERMITIR INGRESO
                    // ====================================

                    _estado.value = _estado.value.copy(
                        mensaje = "Código válido. Asignación encontrada.",
                        ingresoValidado = true,
                        asignacionActiva = asignacion
                    )

                } else {

                    _resultados.value = emptyList()
                    _respuestas.value = emptyList()

                    _estado.value = _estado.value.copy(
                        mensaje = "Código no encontrado.",
                        ingresoValidado = false,
                        asignacionActiva = null
                    )
                }

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
    // 11. ACTIVIDAD DE LECTURA
    // ====================================

    fun seleccionarRespuestaLectura(respuesta: String) {

        val estadoActual = _estadoLectura.value

        // No permitir cambiar la respuesta
        // si ya terminó la actividad
        if (estadoActual.finalizada) return

        // No permitir seleccionar más respuestas
        // si agotó los intentos
        if (estadoActual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoLectura.value = estadoActual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        _estadoLectura.value = estadoActual.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // ====================================
    // 12. COMPROBAR RESPUESTA DE LECTURA
    // ====================================

    fun comprobarRespuestaLectura() {

        val asignacion =
            _estado.value.asignacionActiva ?: return

        // ID 1 = Lectura en la demostración
        if (asignacion.actividadId != 1) return

        val estadoActual = _estadoLectura.value

        // No repetir una actividad completada
        if (estadoActual.finalizada) return

        // ====================================
        // CONTROL DEL LÍMITE DE INTENTOS
        // ====================================

        if (estadoActual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoLectura.value = estadoActual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        val respuesta = estadoActual.respuestaSeleccionada

        if (respuesta == null) {

            _estadoLectura.value = estadoActual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )

            return
        }

        // Continuar desde el último intento guardado
        val nuevosIntentos = estadoActual.intentos + 1

        // Respuesta correcta del ejercicio
        val correcta = respuesta == "Un gato"

        val mensajeResultado = when {

            correcta -> {
                "¡Muy bien! Respuesta correcta."
            }

            nuevosIntentos >= MAX_INTENTOS_DEMO -> {
                "Respuesta incorrecta. Has agotado los 3 intentos permitidos."
            }

            else -> {
                "Respuesta incorrecta. Inténtalo nuevamente."
            }
        }

        // Actualizar progreso
        _estadoLectura.value = estadoActual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = correcta
        )

        viewModelScope.launch {

            try {

                // ====================================
                // GUARDAR CADA RESPUESTA EN ROOM
                // ====================================

                if (respuestaRepository != null) {

                    respuestaRepository.guardarRespuesta(
                        asignacionId = asignacion.id,
                        actividadId = 1,
                        preguntaId = 1,
                        respuestaSeleccionada = respuesta,
                        esCorrecta = correcta,
                        numeroIntento = nuevosIntentos
                    )

                    // Recuperar historial actualizado
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

                // ====================================
                // GUARDAR RESULTADO FINAL
                // ====================================

                if (correcta) {

                    val nuevoResultado = Resultado(
                        id = 0,
                        asignacionId = asignacion.id,
                        aciertos = 1,
                        totalPreguntas = 1,
                        intentos = nuevosIntentos,
                        observacion = "Actividad de Lectura (demostración)"
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
                            mensaje = "No se pudo guardar el intento o resultado."
                        )
                }
            }
        }
    }

    // ====================================
    // 13. ACTIVIDAD DE MATEMÁTICA
    // ====================================

    fun seleccionarRespuestaMatematica(respuesta: String) {

        val estadoActual = _estadoMatematica.value

        // No cambiar una actividad terminada
        if (estadoActual.finalizada) return

        // Bloquear si agotó los intentos
        if (estadoActual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoMatematica.value = estadoActual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        _estadoMatematica.value = estadoActual.copy(
            respuestaSeleccionada = respuesta,
            mensaje = "",
            esCorrecta = null
        )
    }

    // ====================================
    // 14. COMPROBAR RESPUESTA DE MATEMÁTICA
    // ====================================

    fun comprobarRespuestaMatematica() {

        val asignacion =
            _estado.value.asignacionActiva ?: return

        // ID 2 = Matemática en la demostración
        if (asignacion.actividadId != 2) return

        val estadoActual = _estadoMatematica.value

        // No repetir actividad completada
        if (estadoActual.finalizada) return

        // ====================================
        // CONTROL DEL LÍMITE DE INTENTOS
        // ====================================

        if (estadoActual.intentos >= MAX_INTENTOS_DEMO) {

            _estadoMatematica.value = estadoActual.copy(
                mensaje = "Has agotado los 3 intentos permitidos."
            )

            return
        }

        val respuesta = estadoActual.respuestaSeleccionada

        if (respuesta == null) {

            _estadoMatematica.value = estadoActual.copy(
                mensaje = "Selecciona una respuesta.",
                esCorrecta = null
            )

            return
        }

        // Continuar desde el último intento
        val nuevosIntentos = estadoActual.intentos + 1

        // Respuesta correcta del ejercicio
        val correcta = respuesta == "12"

        val mensajeResultado = when {

            correcta -> {
                "¡Muy bien! Respuesta correcta."
            }

            nuevosIntentos >= MAX_INTENTOS_DEMO -> {
                "Respuesta incorrecta. Has agotado los 3 intentos permitidos."
            }

            else -> {
                "Respuesta incorrecta. Inténtalo nuevamente."
            }
        }

        // Actualizar el estado
        _estadoMatematica.value = estadoActual.copy(
            mensaje = mensajeResultado,
            esCorrecta = correcta,
            intentos = nuevosIntentos,
            finalizada = correcta
        )

        viewModelScope.launch {

            try {

                // ====================================
                // GUARDAR RESPUESTA INDIVIDUAL
                // ====================================

                if (respuestaRepository != null) {

                    respuestaRepository.guardarRespuesta(
                        asignacionId = asignacion.id,
                        actividadId = 2,
                        preguntaId = 1,
                        respuestaSeleccionada = respuesta,
                        esCorrecta = correcta,
                        numeroIntento = nuevosIntentos
                    )

                    // Recuperar historial actualizado
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

                // ====================================
                // GUARDAR RESULTADO FINAL
                // ====================================

                if (correcta) {

                    val nuevoResultado = Resultado(
                        id = 0,
                        asignacionId = asignacion.id,
                        aciertos = 1,
                        totalPreguntas = 1,
                        intentos = nuevosIntentos,
                        observacion = "Actividad de Matemática (demostración)"
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
                            mensaje = "No se pudo guardar el intento o resultado."
                        )
                }
            }
        }
    }
}
