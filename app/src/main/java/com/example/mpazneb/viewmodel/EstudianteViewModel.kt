
package com.example.mpazneb.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class IngresoEstudianteUiState(
    val codigoTemporal: String = "",
    val mensaje: String = ""
)

class EstudianteViewModel : ViewModel() {

    private val _estado = MutableStateFlow(
        IngresoEstudianteUiState()
    )

    val estado: StateFlow<IngresoEstudianteUiState> = _estado

    fun actualizarCodigo(codigo: String) {
        _estado.value = _estado.value.copy(
            codigoTemporal = codigo,
            mensaje = ""
        )
    }

    fun intentarIngresar() {
        val codigo = _estado.value.codigoTemporal.trim()

        val nuevoMensaje = if (codigo.isEmpty()) {
            "Debes ingresar un código."
        } else {
            "Código ingresado. Falta validar la asignación."
        }

        _estado.value = _estado.value.copy(
            mensaje = nuevoMensaje
        )
    }
}
