
package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.model.Respuesta

import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity

import com.example.mpazneb.repository.estudiante.DemoEstudianteRepository
import com.example.mpazneb.repository.estudiante.EstudianteRepository
import com.example.mpazneb.repository.estudiante.RespuestaRepository
import com.example.mpazneb.repository.estudiante.ContenidoEstudianteRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val MAX_INTENTOS_DEMO = 3

// ====================================
// ESTADOS DEL ESTUDIANTE
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
// VIEWMODEL DEL ESTUDIANTE
// ====================================

class EstudianteViewModel(
    private val repositorio: EstudianteRepository =
        DemoEstudianteRepository(),

    private val respuestaRepository: RespuestaRepository? = null,

    private val contenidoRepository: ContenidoEstudianteRepository? = null
) : ViewModel() {

    // ====================================
    // ESTADO DE INGRESO
    // ====================================

    private val _estado =
        MutableStateFlow(IngresoEstudianteUiState())

    val estado: StateFlow<IngresoEstudianteUiState> =
        _estado

    // ====================================
    // RESULTADOS
    // ====================================

    private val _resultados =
        MutableStateFlow<List<Resultado>>(emptyList())

    val resultados: StateFlow<List<Resultado>> =
        _resultados

    // ====================================
    // HISTORIAL DE RESPUESTAS
    // ====================================

    private val _respuestas =
        MutableStateFlow<List<Respuesta>>(emptyList())

    val respuestas: StateFlow<List<Respuesta>> =
        _respuestas

    // ====================================
    // NUEVO: GUARDADO DE PREGUNTAS REALES
    // ====================================

    private val _guardandoRespuestaAsignada =
        MutableStateFlow(false)

    val guardandoRespuestaAsignada: StateFlow<Boolean> =
        _guardandoRespuestaAsignada

    private val _mensajeRespuestaAsignada =
        MutableStateFlow("")

    val mensajeRespuestaAsignada: StateFlow<String> =
        _mensajeRespuestaAsignada

    // ====================================
    // ACTIVIDAD ASIGNADA DESDE ROOM
    // ====================================

    private val _actividadAsignada =
        MutableStateFlow<ActividadEntity?>(null)

    val actividadAsignada: StateFlow<ActividadEntity?> =
        _actividadAsignada

    // ====================================
    // PREGUNTAS DE LA ACTIVIDAD
    // ====================================

    private val _preguntasAsignadas =
        MutableStateFlow<List<PreguntaEntity>>(emptyList())

    val preguntasAsignadas: StateFlow<List<PreguntaEntity>> =
        _preguntasAsignadas

    // ====================================
    // ESTADO DE LECTURA
    // ====================================

    private val _estadoLectura =
        MutableStateFlow(LecturaUiState())

    val estadoLectura: StateFlow<LecturaUiState> =
        _estadoLectura

    // ====================================
    // ESTADO DE MATEMÁTICA
    // ====================================

    private val _estadoMatematica =
        MutableStateFlow(MatematicaUiState())

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

        // Limpiar sesión anterior.
        _estadoLectura.value = LecturaUiState()
        _estadoMatematica.value = MatematicaUiState()

        _resultados.value = emptyList()
        _respuestas.value = emptyList()

        _actividadAsignada.value = null
        _preguntasAsignadas.value = emptyList()

        _mensajeRespuestaAsignada.value = ""
    }

    // ====================================
    // 2. VALIDAR CÓDIGO Y RECUPERAR ROOM
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

                // La asignación corresponde
                // al código entregado por el Asistente.
                val asignacion =
                    repositorio.buscarAsignacionPorCodigo(codigo)

                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                if (asignacion == null) {

                    _resultados.value = emptyList()
                    _respuestas.value = emptyList()

                    _actividadAsignada.value = null
                    _preguntasAsignadas.value = emptyList()

                    _estado.value = _estado.value.copy(
                        mensaje = "Código no encontrado.",
                        ingresoValidado = false,
                        asignacionActiva = null
                    )

                    return@launch
                }

                // ====================================
                // RECUPERAR ACTIVIDAD DE ADMINISTRACIÓN
                // ====================================

                val actividadConsultada =
                    contenidoRepository?.buscarActividad(
                        asignacion.actividadId
                    )

                // ====================================
                // RECUPERAR PREGUNTAS DE LA ACTIVIDAD
                // ====================================

                val preguntasConsultadas =
                    if (actividadConsultada != null) {

                        contenidoRepository?.obtenerPreguntas(
                            asignacion.actividadId
                        ) ?: emptyList()

                    } else {

                        emptyList()
                    }

                // ====================================
                // RECUPERAR RESULTADOS
                // ====================================

                val resultadosGuardados =
                    repositorio.obtenerResultados(
                        asignacion.id
                    )

                // ====================================
                // RECUPERAR HISTORIAL DE RESPUESTAS
                // ====================================

                val respuestasGuardadas =
                    respuestaRepository
                        ?.obtenerRespuestasParaMostrar(
                            asignacion.id
                        )
                        ?: emptyList()

                // Evitar mezclar sesiones.
                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                _resultados.value = resultadosGuardados
                _respuestas.value = respuestasGuardadas

                _actividadAsignada.value = actividadConsultada
                _preguntasAsignadas.value = preguntasConsultadas

                // ====================================
                // RECUPERAR INTENTOS DE LECTURA DEMO
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
                // RECUPERAR INTENTOS DE MATEMÁTICA DEMO
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

                val intentosResultadoAnterior =
                    resultadosGuardados.maxOfOrNull {
                        it.intentos
                    } ?: 0

                val resultadoFinal =
                    resultadosGuardados.firstOrNull()

                val acerto =
                    resultadoFinal?.aciertos?.let {
                        it > 0
                    }

                // ====================================
                // RESTAURAR LECTURA DEMOSTRATIVA
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
                // RESTAURAR MATEMÁTICA DEMOSTRATIVA
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

                // ====================================
                // INGRESO CORRECTO
                // ====================================

                _estado.value = _estado.value.copy(
                    mensaje = "Código válido. Asignación encontrada.",
                    ingresoValidado = true,
                    asignacionActiva = asignacion
                )

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                if (
                    _estado.value.codigoTemporal.trim() == codigo
                ) {

                    _estado.value = _estado.value.copy(
                        mensaje = "No se pudo verificar el código.",
                        ingresoValidado = false,
                        asignacionActiva = null
                    )

                    _actividadAsignada.value = null
                    _preguntasAsignadas.value = emptyList()
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

        // Mantener la actividad demostrativa.
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

        val correcta = respuesta == "Un gato"

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

                // Guardar el intento en Room.
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

                // Guardar resultado cuando termina.
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

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

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

        // Mantener la actividad demostrativa.
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

                // Guardar el intento en Room.
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

                // Guardar resultado cuando termina.
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

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

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

    // ====================================
    // 7. RESPONDER PREGUNTAS REALES DE ROOM
    // ====================================

    fun responderPreguntaAsignada(
        preguntaId: Int,
        alternativa: String
    ) {

        if (_guardandoRespuestaAsignada.value) return

        val asignacion =
            _estado.value.asignacionActiva ?: return

        if (!_estado.value.ingresoValidado) return

        val actividad =
            _actividadAsignada.value ?: return

        // La actividad tiene que corresponder
        // a la asignación del estudiante.
        if (actividad.id != asignacion.actividadId) {

            _mensajeRespuestaAsignada.value =
                "La actividad no corresponde a tu asignación."

            return
        }

        val preguntasActividad =
            _preguntasAsignadas.value.filter {
                it.actividadId == actividad.id
            }

        val pregunta =
            preguntasActividad.firstOrNull {
                it.id == preguntaId
            } ?: return

        val opcion = alternativa.trim().uppercase()

        if (opcion !in listOf("A", "B", "C", "D")) {

            _mensajeRespuestaAsignada.value =
                "Selecciona una alternativa."

            return
        }

        val repoRespuestas = respuestaRepository

        if (repoRespuestas == null) {

            _mensajeRespuestaAsignada.value =
                "No se pueden guardar respuestas."

            return
        }

        _guardandoRespuestaAsignada.value = true
        _mensajeRespuestaAsignada.value = ""

        viewModelScope.launch {

            try {

                // ====================================
                // RECUPERAR INTENTOS GUARDADOS
                // ====================================

                val anteriores =
                    repoRespuestas.obtenerRespuestasParaMostrar(
                        asignacion.id
                    )

                val intentosPregunta = anteriores.filter {
                    it.actividadId == actividad.id &&
                            it.preguntaId == pregunta.id
                }

                val yaCorrecta =
                    intentosPregunta.any {
                        it.esCorrecta
                    }

                val ultimoIntento =
                    intentosPregunta.maxOfOrNull {
                        it.numeroIntento
                    } ?: 0

                // Si ya acertó o agotó intentos,
                // no permitir una respuesta adicional.
                if (
                    yaCorrecta ||
                    ultimoIntento >= MAX_INTENTOS_DEMO
                ) {

                    if (
                        _estado.value.asignacionActiva?.id ==
                        asignacion.id
                    ) {
                        _respuestas.value = anteriores
                    }

                    return@launch
                }

                val nuevoIntento = ultimoIntento + 1

                // ====================================
                // COMPROBAR RESPUESTA CORRECTA
                // ====================================

                val esCorrecta =
                    opcion == pregunta.respuestaCorrecta
                        .trim()
                        .uppercase()

                // ====================================
                // GUARDAR RESPUESTA EN ROOM
                // ====================================

                repoRespuestas.guardarRespuesta(
                    asignacionId = asignacion.id,
                    actividadId = actividad.id,
                    preguntaId = pregunta.id,
                    respuestaSeleccionada = opcion,
                    esCorrecta = esCorrecta,
                    numeroIntento = nuevoIntento
                )

                // Recuperar historial actualizado.
                val nuevasRespuestas =
                    repoRespuestas.obtenerRespuestasParaMostrar(
                        asignacion.id
                    )

                if (
                    _estado.value.asignacionActiva?.id ==
                    asignacion.id
                ) {
                    _respuestas.value = nuevasRespuestas
                }

                // ====================================
                // VERIFICAR FINALIZACIÓN
                // ====================================

                val todasFinalizadas =
                    preguntasActividad.isNotEmpty() &&
                            preguntasActividad.all { item ->

                                val intentosItem =
                                    nuevasRespuestas.filter {
                                        it.actividadId == actividad.id &&
                                                it.preguntaId == item.id
                                    }

                                val acertada =
                                    intentosItem.any {
                                        it.esCorrecta
                                    }

                                val totalUsados =
                                    intentosItem.maxOfOrNull {
                                        it.numeroIntento
                                    } ?: 0

                                acertada ||
                                        totalUsados >= MAX_INTENTOS_DEMO
                            }

                // ====================================
                // GUARDAR RESULTADO FINAL
                // ====================================

                if (todasFinalizadas) {

                    val resultadosExistentes =
                        repositorio.obtenerResultados(
                            asignacion.id
                        )

                    // Evitar resultados duplicados.
                    if (resultadosExistentes.isEmpty()) {

                        val aciertos =
                            preguntasActividad.count { item ->

                                nuevasRespuestas.any {
                                    it.actividadId == actividad.id &&
                                            it.preguntaId == item.id &&
                                            it.esCorrecta
                                }
                            }

                        val idsPreguntas =
                            preguntasActividad.map {
                                it.id
                            }.toSet()

                        val totalIntentos =
                            nuevasRespuestas.count {
                                it.actividadId == actividad.id &&
                                        it.preguntaId in idsPreguntas
                            }

                        val resultadoFinal = Resultado(
                            id = 0,
                            asignacionId = asignacion.id,
                            aciertos = aciertos,
                            totalPreguntas = preguntasActividad.size,
                            intentos = totalIntentos,
                            observacion =
                                "Actividad asignada: ${actividad.titulo}"
                        )

                        repositorio.guardarResultado(
                            resultadoFinal
                        )
                    }

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

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                if (
                    _estado.value.asignacionActiva?.id ==
                    asignacion.id
                ) {

                    _mensajeRespuestaAsignada.value =
                        "No se pudo guardar la respuesta o el resultado."
                }

            } finally {

                _guardandoRespuestaAsignada.value = false
            }
        }
    }
}
