package com.example.mpazneb.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.ActividadRequest

import com.example.mpazneb.repository.administracion.ActividadRepository
import com.example.mpazneb.repository.administracion.ActividadRemotaRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AdministracionViewModel(
    private val repositorio: ActividadRepository? = null
) : ViewModel() {

    // ====================================
    // ESTADOS GENERALES
    // ====================================

    var mensaje by mutableStateOf("")
        private set

    // Actividades almacenadas en Room
    var actividades by mutableStateOf<List<Actividad>>(emptyList())
        private set

    // Actividades obtenidas desde Spring Boot
    var actividadesRemotas by mutableStateOf<List<Actividad>>(emptyList())
        private set

    var actividadPreparada by mutableStateOf<Actividad?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    // Estado de las consultas remotas
    var cargandoRemotas by mutableStateOf(false)
        private set

    // Mensajes de Spring Boot
    var mensajeRemoto by mutableStateOf("")
        private set

    // ====================================
    // REPOSITORIOS
    // ====================================

    // Repositorio local Room
    val repo = repositorio

    // Repositorio remoto Spring Boot
    private val repositorioRemoto = ActividadRemotaRepository()

    // ====================================
    // 1. GUARDAR ACTIVIDAD NUEVA EN ROOM
    // ====================================

    fun guardarActividad(
        titulo: String,
        descripcion: String,
        modulo: String,
        curso: Int,
        dificultad: String,
        objetivo: String = "",
        instrucciones: String = ""
    ) {

        if (guardando) return

        // Validar campos obligatorios
        if (
            titulo.isBlank() ||
            descripcion.isBlank() ||
            objetivo.isBlank() ||
            instrucciones.isBlank()
        ) {

            actividadPreparada = null

            mensaje =
                "Completa el título, descripción, objetivo e instrucciones."

            return
        }

        // Validar curso
        if (curso !in 1..4) {

            mensaje = "Selecciona un curso válido."

            return
        }

        // Validar módulo
        if (
            modulo != "Lectura" &&
            modulo != "Matemática"
        ) {

            mensaje = "Selecciona un módulo válido."

            return
        }

        // Validar dificultad
        if (
            dificultad !in listOf(
                "Básica",
                "Media",
                "Avanzada"
            )
        ) {

            mensaje = "Selecciona una dificultad válida."

            return
        }

        // Preparar actividad
        val actividad = Actividad(
            id = 0,
            titulo = titulo.trim(),
            modulo = modulo,
            curso = curso,
            dificultad = dificultad,
            descripcion = descripcion.trim(),
            activa = false,
            objetivo = objetivo.trim(),
            instrucciones = instrucciones.trim()
        )

        actividadPreparada = actividad

        // Mantener el funcionamiento actual de Room
        if (repositorio == null) {

            mensaje =
                "Vista previa: actividad preparada, sin guardar en Room."

            return
        }

        guardando = true

        mensaje = "Guardando actividad..."

        viewModelScope.launch {

            try {

                // Guardar en Room
                repositorio.guardarActividad(actividad)

                // Actualizar listado local
                actividades = repositorio.listarActividades()

                mensaje = "Actividad guardada correctamente."

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje = "No se pudo guardar la actividad."

            } finally {

                guardando = false
            }
        }
    }

    // ====================================
    // 2. CARGAR ACTIVIDADES DESDE ROOM
    // ====================================

    fun cargarActividades() {

        val repo = repositorio ?: return

        viewModelScope.launch {

            try {

                actividades = repo.listarActividades()

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje =
                    "No se pudieron cargar las actividades."
            }
        }
    }

    // ====================================
    // 3. ACTIVAR O DESACTIVAR ACTIVIDAD
    // ====================================

    fun cambiarEstadoActividad(
        actividad: Actividad
    ) {

        if (guardando) return

        val repo = repositorio ?: run {

            mensaje = "No hay conexión con Room."

            return
        }

        guardando = true

        viewModelScope.launch {

            try {

                val nuevoEstado = !actividad.activa

                val actualizado =
                    repo.actualizarEstadoActividad(
                        id = actividad.id,
                        activa = nuevoEstado
                    )

                if (actualizado) {

                    actividades = repo.listarActividades()

                    mensaje = if (nuevoEstado) {

                        "Actividad activada correctamente."

                    } else {

                        "Actividad desactivada correctamente."
                    }

                } else {

                    mensaje = if (nuevoEstado) {

                        "No se pudo activar. Agrega al menos una pregunta."

                    } else {

                        "No se encontró la actividad."
                    }
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje =
                    "No se pudo actualizar el estado."

            } finally {

                guardando = false
            }
        }
    }

    // ====================================
    // 4. EDITAR ACTIVIDAD EXISTENTE
    // ====================================

    fun editarActividad(
        actividad: Actividad,
        titulo: String,
        descripcion: String,
        curso: Int,
        dificultad: String,
        objetivo: String,
        instrucciones: String
    ) {

        if (guardando) return

        // Validar campos obligatorios
        if (
            titulo.isBlank() ||
            descripcion.isBlank() ||
            objetivo.isBlank() ||
            instrucciones.isBlank()
        ) {

            mensaje =
                "Completa el título, descripción, objetivo e instrucciones."

            return
        }

        // Validar curso
        if (curso !in 1..4) {

            mensaje = "Selecciona un curso válido."

            return
        }

        // Validar dificultad
        if (
            dificultad !in listOf(
                "Básica",
                "Media",
                "Avanzada"
            )
        ) {

            mensaje = "Selecciona una dificultad válida."

            return
        }

        val repo = repositorio ?: run {

            mensaje = "No hay conexión con Room."

            return
        }

        guardando = true

        mensaje = "Guardando cambios..."

        viewModelScope.launch {

            try {

                val actualizado =
                    repo.editarActividad(
                        id = actividad.id,
                        titulo = titulo.trim(),
                        descripcion = descripcion.trim(),
                        curso = curso,
                        dificultad = dificultad,
                        objetivo = objetivo.trim(),
                        instrucciones = instrucciones.trim()
                    )

                if (actualizado) {

                    actividades = repo.listarActividades()

                    mensaje =
                        "Actividad editada correctamente."

                } else {

                    mensaje =
                        "No se encontró la actividad para editar."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: IllegalStateException) {

                mensaje =
                    "No se pudo editar: ${error.message}"

            } catch (error: Exception) {

                mensaje =
                    "No se pudo editar la actividad."

            } finally {

                guardando = false
            }
        }
    }

    // ====================================
    // 5. CONSULTAR ACTIVIDADES SPRING BOOT
    // ====================================

    fun cargarActividadesRemotas() {

        if (cargandoRemotas || guardando) return

        cargandoRemotas = true

        mensajeRemoto =
            "Consultando actividades del servidor..."

        viewModelScope.launch {

            try {

                // Consultar Spring Boot mediante Retrofit
                val resultado =
                    repositorioRemoto.obtenerActividades()

                // Mantener las actividades remotas separadas
                actividadesRemotas = resultado

                mensajeRemoto =
                    if (resultado.isEmpty()) {

                        "No hay actividades guardadas en el servidor."

                    } else {

                        "Actividades del servidor cargadas correctamente."
                    }

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensajeRemoto =
                    "No se pudo conectar con Spring Boot. " +
                            "Verifica que la API esté encendida."

            } finally {

                cargandoRemotas = false
            }
        }
    }

    // ====================================
    // 6. CREAR ACTIVIDAD EN SPRING BOOT
    // ====================================

    fun crearActividadRemota(
        titulo: String,
        descripcion: String,
        modulo: String,
        curso: Int,
        dificultad: String,
        objetivo: String,
        instrucciones: String
    ) {

        if (guardando || cargandoRemotas) return

        // Validar campos obligatorios
        if (
            titulo.isBlank() ||
            descripcion.isBlank() ||
            objetivo.isBlank() ||
            instrucciones.isBlank()
        ) {

            mensajeRemoto =
                "Completa todos los campos obligatorios."

            return
        }

        // Validar módulo
        if (
            modulo !in listOf(
                "Lectura",
                "Matemática"
            )
        ) {

            mensajeRemoto =
                "Selecciona un módulo válido."

            return
        }

        // Validar curso
        if (curso !in 1..4) {

            mensajeRemoto =
                "Selecciona un curso entre primero y cuarto básico."

            return
        }

        // Validar dificultad
        if (
            dificultad !in listOf(
                "Básica",
                "Media",
                "Avanzada"
            )
        ) {

            mensajeRemoto =
                "Selecciona una dificultad válida."

            return
        }

        // Preparar solicitud JSON
        val solicitud = ActividadRequest(
            titulo = titulo.trim(),
            modulo = modulo,
            curso = curso,
            dificultad = dificultad,
            descripcion = descripcion.trim(),
            objetivo = objetivo.trim(),
            instrucciones = instrucciones.trim()
        )

        guardando = true

        mensajeRemoto =
            "Guardando actividad en el servidor..."

        viewModelScope.launch {

            try {

                // ====================================
                // GUARDAR MEDIANTE SPRING BOOT
                // ====================================

                val actividadCreada =
                    repositorioRemoto.crearActividad(solicitud)

                // Confirmar que Spring Boot respondió
                // correctamente a la creación.
                mensajeRemoto =
                    "Actividad '${actividadCreada.titulo}' " +
                            "guardada en PostgreSQL con ID ${actividadCreada.id}."

                // ====================================
                // ACTUALIZAR LISTADO REMOTO
                // ====================================

                try {

                    actividadesRemotas =
                        repositorioRemoto.obtenerActividades()

                } catch (error: CancellationException) {

                    throw error

                } catch (error: Exception) {

                    // El servidor confirmó la creación.
                    // Únicamente falló la consulta posterior.

                    mensajeRemoto =
                        "Actividad guardada con ID ${actividadCreada.id}. " +
                                "No se pudo actualizar el listado. " +
                                "Presiona Actualizar actividades."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: HttpException) {

                mensajeRemoto = when (error.code()) {
                    400 -> "Solicitud rechazada: datos pedagógicos inválidos."
                    409 -> "No se pudo guardar la actividad en el servidor por un conflicto."
                    else -> "Error del servidor HTTP ${error.code()}."
                }

            } catch (error: Exception) {

                mensajeRemoto =
                    "No se pudo confirmar el guardado en el servidor. " +
                            "Consulta el listado antes de volver a intentar."

            } finally {

                guardando = false
            }
        }
    }

    // ====================================
    // 7. EDITAR ACTIVIDAD EN SPRING BOOT
    // ====================================

    fun editarActividadRemota(
        actividad: Actividad,
        titulo: String,
        descripcion: String,
        curso: Int,
        dificultad: String,
        objetivo: String,
        instrucciones: String
    ) {

        if (guardando || cargandoRemotas) return

        if (
            titulo.isBlank() ||
            descripcion.isBlank() ||
            objetivo.isBlank() ||
            instrucciones.isBlank()
        ) {
            mensajeRemoto = "Completa todos los campos obligatorios."
            return
        }

        if (curso !in 1..4) {
            mensajeRemoto = "Selecciona un curso válido."
            return
        }

        if (
            dificultad !in listOf(
                "Básica",
                "Media",
                "Avanzada"
            )
        ) {
            mensajeRemoto = "Selecciona una dificultad válida."
            return
        }

        if (actividadesRemotas.none { it.id == actividad.id }) {
            mensajeRemoto = "Selecciona una actividad del servidor."
            return
        }

        guardando = true

        mensajeRemoto = "Guardando cambios en PostgreSQL..."

        viewModelScope.launch {

            try {

                val actividadActual =
                    repositorioRemoto.obtenerActividadPorId(
                        actividad.id
                    )

                if (actividadActual.activa) {
                    mensajeRemoto = "Desactiva la actividad en el servidor antes de editarla."
                    return@launch
                }

                val actividadEditada = actividadActual.copy(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    curso = curso,
                    dificultad = dificultad,
                    objetivo = objetivo.trim(),
                    instrucciones = instrucciones.trim()
                )

                val resultado =
                    repositorioRemoto.actualizarActividad(
                        actividadEditada
                    )

                actividadesRemotas = actividadesRemotas.map {
                    if (it.id == resultado.id) resultado else it
                }

                mensajeRemoto =
                    "Actividad editada correctamente en PostgreSQL."

            } catch (error: CancellationException) {

                throw error

            } catch (error: HttpException) {

                mensajeRemoto = when (error.code()) {
                    400 -> "Solicitud rechazada: datos pedagógicos inválidos."
                    404 -> "La actividad ya no existe en el servidor."
                    409 -> "No se pudo editar: la actividad está activa o tiene registros asociados."
                    else -> "Error del servidor (${error.code()})."
                }

            } catch (error: Exception) {

                mensajeRemoto =
                    "No se pudo confirmar la edición. " +
                            "Actualiza el listado antes de reintentar."

            } finally {

                guardando = false
            }
        }
    }

    // ====================================
    // 8. ACTIVAR O DESACTIVAR EN SPRING BOOT
    // ====================================

    fun cambiarEstadoActividadRemota(
        actividad: Actividad
    ) {

        if (guardando || cargandoRemotas) return

        if (actividadesRemotas.none { it.id == actividad.id }) {
            mensajeRemoto = "Selecciona una actividad del servidor."
            return
        }

        guardando = true

        mensajeRemoto = "Actualizando estado en el servidor..."

        viewModelScope.launch {

            try {

                val actividadActual =
                    repositorioRemoto.obtenerActividadPorId(
                        actividad.id
                    )

                val nuevoEstado = !actividadActual.activa

                // Validar que tenga preguntas antes de activar
                if (nuevoEstado) {

                    val preguntas =
                        com.example.mpazneb.data.remote.RetrofitClient
                            .api.obtenerPreguntasPorActividad(
                                actividad.id
                            )

                    if (preguntas.isEmpty()) {

                        mensajeRemoto =
                            "No se puede activar. " +
                                    "Agrega al menos una pregunta en el servidor."

                        return@launch
                    }
                }

                val resultado =
                    repositorioRemoto.actualizarActividad(
                        actividadActual.copy(
                            activa = nuevoEstado
                        )
                    )

                actividadesRemotas = actividadesRemotas.map {
                    if (it.id == resultado.id) resultado else it
                }

                mensajeRemoto = if (resultado.activa) {

                    "Actividad activada correctamente en AWS."

                } else {

                    "Actividad desactivada correctamente en AWS."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: HttpException) {

                mensajeRemoto = when (error.code()) {
                    409 -> "No se puede cambiar el estado. Agrega al menos una pregunta o verifica asignaciones."
                    404 -> "La actividad ya no existe en el servidor."
                    else -> "Error del servidor HTTP ${error.code()}."
                }

            } catch (error: Exception) {

                mensajeRemoto =
                    "No se pudo confirmar el cambio de estado. " +
                            "Actualiza el listado antes de reintentar."

            } finally {

                guardando = false
            }
        }
    }


    // ====================================
    // 9. ELIMINAR ACTIVIDAD REMOTA
    // ====================================

    fun eliminarActividadRemota(
        actividad: Actividad
    ) {

        if (guardando || cargandoRemotas) return

        if (actividadesRemotas.none { it.id == actividad.id }) {
            mensajeRemoto = "Selecciona una actividad del servidor."
            return
        }

        if (actividad.activa) {
            mensajeRemoto =
                "Desactiva la actividad antes de eliminarla."
            return
        }

        guardando = true

        mensajeRemoto = "Eliminando actividad del servidor..."

        viewModelScope.launch {

            try {

                repositorioRemoto.eliminarActividad(
                    actividad.id
                )

                actividadesRemotas = actividadesRemotas.filter {
                    it.id != actividad.id
                }

                mensajeRemoto =
                    "Actividad eliminada correctamente de PostgreSQL."

            } catch (error: CancellationException) {

                throw error

            } catch (error: retrofit2.HttpException) {

                mensajeRemoto = when (error.code()) {

                    404 ->
                        "La actividad ya no existe en el servidor."

                    409 ->
                        "No se puede eliminar. La actividad esta activa " +
                        "o tiene registros asociados."

                    else ->
                        "Error del servidor: ${error.code()}."
                }

            } catch (error: Exception) {

                mensajeRemoto =
                    "No se pudo confirmar la eliminacion. " +
                    "Actualiza el listado antes de reintentar."

            } finally {

                guardando = false
            }
        }
    }

}
