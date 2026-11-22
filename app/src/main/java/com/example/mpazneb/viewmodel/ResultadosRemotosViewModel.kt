package com.example.mpazneb.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.mpazneb.repository.administracion.ResultadoRemotoRepository
import com.example.mpazneb.repository.administracion.ResumenPedagogicoRemoto

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

import retrofit2.HttpException
import java.io.IOException

class ResultadosRemotosViewModel(
    private val repositorio: ResultadoRemotoRepository =
        ResultadoRemotoRepository()
) : ViewModel() {

    // ====================================
    // 1. ESTADOS DE LA INTERFAZ
    // ====================================

    var resumen by mutableStateOf<ResumenPedagogicoRemoto?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var mensaje by mutableStateOf("")
        private set

    var errorConsulta by mutableStateOf(false)
        private set

    // ====================================
    // 2. CONSULTAR RESULTADOS REMOTOS
    // ====================================

    fun cargarResultados() {

        if (cargando) return

        cargando = true
        errorConsulta = false

        mensaje = "Consultando resultados del servidor..."

        viewModelScope.launch {

            try {

                val nuevoResumen =
                    repositorio.obtenerResumen()

                resumen = nuevoResumen

                mensaje = if (
                    nuevoResumen.detalles.isEmpty()
                ) {
                    "Todavia no hay resultados registrados."
                } else {
                    "Resultados actualizados correctamente."
                }

            } catch (error: CancellationException) {

                throw error

            } catch (error: HttpException) {

                errorConsulta = true

                mensaje =
                    "Error del servidor: ${error.code()}."

            } catch (error: IOException) {

                errorConsulta = true

                mensaje =
                    "No se pudo conectar con el servidor. " +
                    "Comprueba tu conexion a Internet."

            } catch (error: Exception) {

                errorConsulta = true

                mensaje =
                    "No se pudieron consultar los resultados."

            } finally {

                cargando = false
            }
        }
    }
}