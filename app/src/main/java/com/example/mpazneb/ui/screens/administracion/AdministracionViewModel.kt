
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.repository.administracion.ActividadRepository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class AdministracionViewModel(
    private val repositorio: ActividadRepository? = null
) : ViewModel() {

    var mensaje by mutableStateOf("")
        private set

    var actividades by mutableStateOf<List<Actividad>>(emptyList())
        private set

    var actividadPreparada by mutableStateOf<Actividad?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    // Mantener compatibilidad con el código existente
    val repo = repositorio

    // ====================================
    // 1. GUARDAR ACTIVIDAD NUEVA
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
                "Completa el título, descripción, " +
                        "objetivo e instrucciones."

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

        // Crear actividad con objetivo e instrucciones
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

        val repo = repositorio

        if (repo == null) {

            mensaje =
                "Vista previa: actividad preparada, " +
                        "sin guardar en Room."

            return
        }

        guardando = true
        mensaje = "Guardando actividad..."

        viewModelScope.launch {

            try {

                repo.guardarActividad(actividad)

                // Actualizar lista desde Room
                actividades = repo.listarActividades()

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

                mensaje = "No se pudieron cargar las actividades."
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

                // El DAO impide activar una actividad
                // que todavía no tiene preguntas.

                val actualizado =
                    repo.actualizarEstadoActividad(
                        id = actividad.id,
                        activa = nuevoEstado
                    )

                if (actualizado) {

                    // Actualizar listado después del cambio.
                    actividades = repo.listarActividades()

                    mensaje = if (nuevoEstado) {
                        "Actividad activada correctamente."
                    } else {
                        "Actividad desactivada correctamente."
                    }

                } else {

                    mensaje = if (nuevoEstado) {
                        "No se pudo activar la actividad. " +
                                "Comprueba que exista y agrega " +
                                "al menos una pregunta."
                    } else {
                        "No se encontró la actividad."
                    }
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: Exception) {

                mensaje =
                    "No se pudo actualizar el estado de la actividad."

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

        if (
            titulo.isBlank() ||
            descripcion.isBlank() ||
            objetivo.isBlank() ||
            instrucciones.isBlank()
        ) {
            mensaje =
                "Completa el título, descripción, " +
                        "objetivo e instrucciones."
            return
        }

        if (curso !in 1..4) {
            mensaje = "Selecciona un curso válido."
            return
        }

        if (
            dificultad !in listOf(
                "Básica",
                "Media",
                "Avanzada"
            )
        ) {
            mensaje =
                "Selecciona una dificultad válida."
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

                val actualizado = repo.editarActividad(
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
                    mensaje = "Actividad editada correctamente."

                } else {

                    mensaje =
                        "No se encontró la actividad para editar."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: IllegalStateException) {

                mensaje =
                    "No se pudo editar la actividad. " +
                            (error.message ?: "Edición bloqueada.")

            } catch (error: Exception) {

                mensaje =
                    "No se pudo editar la actividad."

            } finally {

                guardando = false
            }
        }
    }

}
