package com.example.mpazneb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.asistente.*

import com.example.mpazneb.repository.asistente.AsistenteDataSource
import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.asistente.ActividadAula
import kotlinx.coroutines.CancellationException


import java.util.Locale
import java.util.UUID

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.mpazneb.model.asistente.AsignacionRequest
import com.example.mpazneb.repository.asistente.AsignacionRemotaRepository

class AsistenteViewModel(
    private val fuente: AsistenteDataSource,
    private val repositorioRemoto: AsignacionRemotaRepository? = null
) : ViewModel() {


    // ACTIVIDADES OBTENIDAS DESDE AWS
    var actividadesRemotas by mutableStateOf<List<ActividadAula>>(emptyList())
        private set

    var cargandoActividadesRemotas by mutableStateOf(false)
        private set

    var errorActividadesRemotas by mutableStateOf("")
        private set

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
    // ASIGNACIONES OBTENIDAS DESDE SPRING BOOT
    var asignacionesRemotas by mutableStateOf<List<AsignacionAula>>(emptyList())
        private set
    var asignacionesRemotasCargadas by mutableStateOf(false)
        private set

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
    // CONSULTAR ACTIVIDADES DISPONIBLES EN AWS
    fun cargarActividadesRemotas() {

        if (cargandoActividadesRemotas) return

        cargandoActividadesRemotas = true
        errorActividadesRemotas = ""

        viewModelScope.launch {
            try {
                val actividadesServidor =
                    RetrofitClient.api.obtenerActividades()

                actividadesRemotas = actividadesServidor
                    .filter { it.activa }
                    .map { actividad ->
                        ActividadAula(
                            id = actividad.id,
                            titulo = actividad.titulo,
                            modulo = actividad.modulo,
                            curso = actividad.curso,
                            dificultad = actividad.dificultad,
                            descripcion = actividad.descripcion,
                            estado = "Disponible"
                        )
                    }

            } catch (error: CancellationException) {
                throw error

            } catch (error: Exception) {
                errorActividadesRemotas =
                    "No se pudieron cargar las actividades de AWS: " +
                            (error.message ?: "error de conexión")

            } finally {
                cargandoActividadesRemotas = false
            }
        }
    }

    // LIMPIAR MENSAJE
    fun limpiarMensaje() {
        mensaje = ""
    }
    // CONSULTAR ASIGNACIONES GUARDADAS EN SPRING BOOT
    fun cargarAsignacionesRemotas() {

        if (trabajando || repositorioRemoto == null) return

        trabajando = true

        viewModelScope.launch {
            try {
                asignacionesRemotas = repositorioRemoto.obtenerAsignaciones()
                asignacionesRemotasCargadas = true

            } catch (e: Exception) {
                mensaje = "No se pudieron cargar las asignaciones del servidor: ${e.message ?: "error de conexión"}"

            } finally {
                trabajando = false
            }
        }
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
    // PRESELECCIONAR ACTIVIDAD DESDE EL CATALOGO
    fun prepararActividad(id: Int) {

        val actividad = actividades.value.firstOrNull {
            it.id == id
        } ?: return

        formulario = formulario.copy(
            modulo = actividad.modulo,
            curso = actividad.curso,
            dificultad = actividad.dificultad,
            actividadId = actividad.id
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

                if (repositorioRemoto != null) {

                    // CREAR ASIGNACION EN SPRING BOOT
                    val solicitud = AsignacionRequest(
                        actividadId = actividad.id,
                        actividadTitulo = actividad.titulo,
                        estudianteAlias = datos.estudianteAlias,
                        asistenteAlias = "ASISTENTE_DEMO",
                        curso = datos.curso
                    )

                    val asignacionGuardada =
                        repositorioRemoto.crearAsignacion(solicitud)

                    // MOSTRAR EL CODIGO GENERADO POR EL SERVIDOR
                    mensaje =
                        "Actividad asignada correctamente. Código: ${asignacionGuardada.codigoTemporal}"

                } else {

                    // CONSERVAR EL FUNCIONAMIENTO LOCAL
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
                        "Actividad asignada correctamente. Código: $codigo"
                }
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
