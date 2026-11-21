
package com.example.mpazneb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.asistente.*
import com.example.mpazneb.repository.asistente.AsistenteDataSource
import com.example.mpazneb.repository.asistente.AsignacionRemotaRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import java.util.Locale
import java.util.UUID

class AsistenteViewModel(
    private val fuente: AsistenteDataSource,
    private val repositorioRemoto: AsignacionRemotaRepository? = null
) : ViewModel() {

    // DATOS LOCALES DE ROOM

    val actividades = fuente.actividades.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val estudiantes = fuente.estudiantes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val asignaciones = fuente.asignaciones.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val resultados = fuente.resultados.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    // DATOS REMOTOS DE AWS

    var actividadesRemotas by mutableStateOf<List<ActividadAula>>(
        emptyList()
    )
        private set

    var asignacionesRemotas by mutableStateOf<List<AsignacionAula>>(
        emptyList()
    )
        private set

    var resultadosRemotos by mutableStateOf<List<ResultadoAula>>(
        emptyList()
    )
        private set

    var asignacionesRemotasCargadas by mutableStateOf(false)
        private set

    var cargandoActividadesRemotas by mutableStateOf(false)
        private set

    var errorActividadesRemotas by mutableStateOf("")
        private set

    var errorResultadosRemotos by mutableStateOf("")
        private set

    // ESTADOS DEL FORMULARIO

    var formulario by mutableStateOf(FormularioAsignacion())
        private set

    var mensaje by mutableStateOf("")
        private set

    var trabajando by mutableStateOf(false)
        private set

    val actividadesParaAsignar: List<ActividadAula>
        get() = if (repositorioRemoto != null) {
            actividadesRemotas
        } else {
            actividades.value
        }

    // CONSULTAR ACTIVIDADES DESDE AWS

    fun cargarActividadesRemotas() {

        val repositorio = repositorioRemoto ?: return

        if (cargandoActividadesRemotas) return

        cargandoActividadesRemotas = true
        errorActividadesRemotas = ""

        viewModelScope.launch {

            try {

                actividadesRemotas =
                    repositorio.obtenerActividades()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                errorActividadesRemotas =
                    "No se pudieron cargar las actividades: " +
                            (e.message ?: "error de conexión")

            } finally {

                cargandoActividadesRemotas = false
            }
        }
    }

    // CONSULTAR ASIGNACIONES Y RESULTADOS DESDE AWS

    fun cargarAsignacionesRemotas() {

        val repositorio = repositorioRemoto ?: return

        if (trabajando) return

        trabajando = true
        errorResultadosRemotos = ""

        viewModelScope.launch {

            try {

                asignacionesRemotas =
                    repositorio.obtenerAsignaciones()

                asignacionesRemotasCargadas = true

                resultadosRemotos = emptyList()

                val resultadosConsultados =
                    mutableListOf<ResultadoAula>()

                var huboError = false

                for (asignacion in asignacionesRemotas) {

                    try {

                        resultadosConsultados.addAll(
                            repositorio.obtenerResultados(
                                asignacion.id
                            )
                        )

                    } catch (e: CancellationException) {
                        throw e

                    } catch (e: Exception) {

                        huboError = true
                    }
                }

                resultadosRemotos = resultadosConsultados

                if (huboError) {

                    errorResultadosRemotos =
                        "No se pudieron consultar algunos resultados. " +
                                "Presiona Actualizar para reintentar."
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                mensaje =
                    "No se pudieron cargar las asignaciones: " +
                            (e.message ?: "error de conexión")

            } finally {

                trabajando = false
            }
        }
    }

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

    // ELEGIR ACTIVIDAD

    fun elegirActividad(id: Int) {

        formulario = formulario.copy(
            actividadId = id
        )
    }

    // ELEGIR ESTUDIANTE

    fun elegirEstudiante(alias: String) {

        formulario = formulario.copy(
            estudianteAlias = alias
        )
    }

    // PRESELECCIONAR ACTIVIDAD DESDE EL CATALOGO

    fun prepararActividad(id: Int) {

        val actividad =
            actividadesParaAsignar.firstOrNull {
                it.id == id
            } ?: return

        formulario = formulario.copy(
            modulo = actividad.modulo,
            curso = actividad.curso,
            dificultad = actividad.dificultad,
            actividadId = actividad.id
        )
    }

    // CREAR ASIGNACION

    fun asignarActividad() {

        if (trabajando) return

        val datos = formulario

        val disponibles = actividadesParaAsignar

        val error = ValidadorAsignacion.validar(
            datos,
            disponibles,
            estudiantes.value
        )

        if (error != null) {

            mensaje = error
            return
        }

        val actividad = disponibles.first {
            it.id == datos.actividadId
        }

        trabajando = true

        viewModelScope.launch {

            try {

                if (repositorioRemoto != null) {

                    // GUARDAR EN SPRING BOOT Y POSTGRESQL

                    val solicitud = AsignacionRequest(
                        actividadId = actividad.id,
                        actividadTitulo = actividad.titulo,
                        estudianteAlias = datos.estudianteAlias,
                        asistenteAlias = "ASISTENTE_DEMO",
                        curso = datos.curso
                    )

                    val guardada =
                        repositorioRemoto.crearAsignacion(
                            solicitud
                        )

                    // ACTUALIZAR ASIGNACIONES Y CONTADORES

                    asignacionesRemotas =
                        asignacionesRemotas.filterNot {
                            it.id == guardada.id
                        } + guardada

                    mensaje =
                        "Actividad asignada correctamente. " +
                                "Código: ${guardada.codigoTemporal}"

                } else {

                    // CONSERVAR FUNCIONAMIENTO LOCAL

                    val codigo = UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .take(10)
                        .uppercase(Locale.ROOT)

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
                        "Actividad asignada correctamente. " +
                                "Código: $codigo"
                }

                formulario = FormularioAsignacion()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                mensaje =
                    "No se pudo guardar la asignación: " +
                            (e.message ?: "error de conexión")

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

        val error =
            ValidadorAsignacion.validarObservacion(texto)

        if (error != null) {

            mensaje = error
            return
        }

        trabajando = true

        viewModelScope.launch {

            try {

                if (repositorioRemoto != null) {

                    // ACTUALIZAR OBSERVACION EN POSTGRESQL

                    val actualizado =
                        repositorioRemoto.guardarObservacion(
                            resultadoId,
                            texto.trim()
                        )

                    resultadosRemotos =
                        resultadosRemotos.map { resultado ->

                            if (resultado.id == actualizado.id) {

                                actualizado

                            } else {

                                resultado
                            }
                        }

                } else {

                    // GUARDADO LOCAL PARA ROOM

                    fuente.guardarObservacion(
                        resultadoId,
                        texto.trim()
                    )
                }

                mensaje =
                    "Observación guardada correctamente."

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {

                mensaje =
                    "No se pudo guardar la observación: " +
                            (e.message ?: "error de conexión")

            } finally {

                trabajando = false
            }
        }
    }
}

// FACTORY DEL VIEWMODEL

class AsistenteViewModelFactory(
    private val fuente: AsistenteDataSource,
    private val repositorioRemoto: AsignacionRemotaRepository? = null
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
            return AsistenteViewModel(
                fuente = fuente,
                repositorioRemoto = repositorioRemoto
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
