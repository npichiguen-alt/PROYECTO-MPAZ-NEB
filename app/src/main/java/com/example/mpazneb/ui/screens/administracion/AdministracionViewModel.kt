
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.mpazneb.model.Actividad

class AdministracionViewModel : ViewModel() {

    var mensaje by mutableStateOf("")
        private set

    var actividadPreparada by mutableStateOf<Actividad?>(null)
        private set

    fun validarFormulario(
        titulo: String,
        descripcion: String,
        modulo: String,
        curso: Int,
        dificultad: String
    ) {

        if (titulo.isBlank() || descripcion.isBlank()) {
            actividadPreparada = null
            mensaje = "Completa el título y la descripción."
            return
        }

        actividadPreparada = Actividad(
            id = 0,
            titulo = titulo.trim(),
            modulo = modulo,
            curso = curso,
            dificultad = dificultad,
            descripcion = descripcion.trim(),
            activa = false
        )

        mensaje = "Actividad preparada: ${titulo.trim()}. Pendiente guardar en Room."
    }
}
