
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

    val repo = repositorio
    fun guardarActividad(
        titulo: String,
        descripcion: String,
        modulo: String,
        curso: Int,
        dificultad: String
    ) {
        if (guardando) return

        if (titulo.isBlank() || descripcion.isBlank()) {
            actividadPreparada = null
            mensaje = "Completa el título y la descripción."
            return
        }

        val actividad = Actividad(
            id = 0,
            titulo = titulo.trim(),
            modulo = modulo,
            curso = curso,
            dificultad = dificultad,
            descripcion = descripcion.trim(),
            activa = false
        )

        actividadPreparada = actividad

        val repo = repositorio

        if (repo == null) {
            mensaje = "Vista previa: actividad preparada, sin guardar en Room."
            return
        }

        guardando = true
        mensaje = "Guardando actividad..."

        viewModelScope.launch {
            try {
                repo.guardarActividad(actividad)

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

}
