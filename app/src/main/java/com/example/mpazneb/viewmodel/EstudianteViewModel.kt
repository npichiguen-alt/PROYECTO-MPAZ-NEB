package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.PreguntaEntity

import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.model.Respuesta

import com.example.mpazneb.repository.estudiante.EstudianteRepository
import com.example.mpazneb.repository.estudiante.RespuestaRepository
import com.example.mpazneb.repository.estudiante.ContenidoEstudianteRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val MAX_INTENTOS_POR_PREGUNTA = 3

// ====================================
// ESTADO DE INGRESO DEL ESTUDIANTE
// ====================================

data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = "",
    val ingresoValidado: Boolean = false,
    val asignacionActiva: Asignacion? = null
)

// ====================================
// VIEWMODEL DEL ESTUDIANTE
// ====================================

class EstudianteViewModel(
    private val repositorio: EstudianteRepository,
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
    // ESTADO DEL GUARDADO DE RESPUESTAS
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
    // ACTIVIDAD REAL ASIGNADA DESDE ROOM
    // ====================================

    private val _actividadAsignada =
        MutableStateFlow<ActividadEntity?>(null)

    val actividadAsignada: StateFlow<ActividadEntity?> =
        _actividadAsignada

    // ====================================
    // PREGUNTAS REALES DE LA ACTIVIDAD
    // ====================================

    private val _preguntasAsignadas =
        MutableStateFlow<List<PreguntaEntity>>(emptyList())

    val preguntasAsignadas: StateFlow<List<PreguntaEntity>> =
        _preguntasAsignadas

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

        // Limpiar datos de la sesión anterior.

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

                // Buscar asignación real del Asistente.

                val asignacion =
                    repositorio.buscarAsignacionPorCodigo(codigo)

                // Evitar recuperar una sesión anterior.

                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                // Código inexistente.

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
                // RECUPERAR PREGUNTAS REALES
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
                // RECUPERAR RESULTADOS GUARDADOS
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

                // Verificar que el código no cambió
                // durante las consultas.

                if (
                    _estado.value.codigoTemporal.trim() != codigo
                ) {
                    return@launch
                }

                // Actualizar estados con datos de Room.

                _resultados.value = resultadosGuardados
                _respuestas.value = respuestasGuardadas

                _actividadAsignada.value = actividadConsultada
                _preguntasAsignadas.value = preguntasConsultadas

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
    // 3. RESPONDER PREGUNTAS REALES DE ROOM
    // ====================================

    // ====================================
    // RESPONDER PREGUNTAS REALES
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

        // Verificar que la actividad corresponde
        // a la asignación del estudiante.

        if (actividad.id != asignacion.actividadId) {

            _mensajeRespuestaAsignada.value =
                "La actividad no corresponde a tu asignación."

            return
        }

        // Recuperar preguntas reales.

        val preguntasActividad =
            _preguntasAsignadas.value.filter {
                it.actividadId == actividad.id
            }

        val pregunta =
            preguntasActividad.firstOrNull {
                it.id == preguntaId
            }

        if (pregunta == null) {

            _mensajeRespuestaAsignada.value =
                "No se encontró la pregunta."

            return
        }

        // Validar alternativa.

        val opcion = alternativa
            .trim()
            .uppercase()

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

        // Comprobar respuesta correcta.

        val esCorrecta =
            opcion == pregunta.respuestaCorrecta
                .trim()
                .uppercase()

        // Identificadores de todas las preguntas
        // de la actividad asignada.

        val preguntasIds =
            preguntasActividad.map {
                it.id
            }

        _guardandoRespuestaAsignada.value = true
        _mensajeRespuestaAsignada.value = ""

        viewModelScope.launch {

            try {

                // ====================================
                // GUARDAR MEDIANTE TRANSACCIÓN ROOM
                // ====================================

                val respuestaRegistrada =
                    repoRespuestas.guardarRespuestaYResultado(
                        asignacionId = asignacion.id,
                        actividadId = actividad.id,
                        preguntaId = pregunta.id,
                        respuestaSeleccionada = opcion,
                        esCorrecta = esCorrecta,
                        preguntasIds = preguntasIds
                    )

                // ====================================
                // RECUPERAR HISTORIAL ACTUALIZADO
                // ====================================

                val respuestasActualizadas =
                    repoRespuestas.obtenerRespuestasParaMostrar(
                        asignacion.id
                    )

                // ====================================
                // RECUPERAR RESULTADOS ACTUALIZADOS
                // ====================================

                val resultadosActualizados =
                    repositorio.obtenerResultados(
                        asignacion.id
                    )

                // Evitar mezclar asignaciones cuando
                // el estudiante cambió de sesión.

                if (
                    _estado.value.asignacionActiva?.id ==
                    asignacion.id
                ) {

                    _respuestas.value =
                        respuestasActualizadas

                    _resultados.value =
                        resultadosActualizados

                    if (!respuestaRegistrada) {

                        _mensajeRespuestaAsignada.value =
                            "Esta pregunta ya fue finalizada " +
                                    "o la actividad está completada."
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
                        "No se pudo guardar la respuesta. " +
                                "Inténtalo nuevamente."
                }

            } finally {

                _guardandoRespuestaAsignada.value = false
            }
        }
    }

}
