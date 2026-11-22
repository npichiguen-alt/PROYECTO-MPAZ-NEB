package com.example.mpazneb.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpazneb.repository.pokemon.PokemonRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class PokemonViewModel : ViewModel() {

    var imagenUrl by mutableStateOf<String?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    var error by mutableStateOf(false)
        private set

    private var nombreActual: String? = null

    fun cargar(nombre: String, forzar: Boolean = false) {
        val nombreNormalizado = nombre.lowercase().trim()

        if (cargando) return
        if (!forzar && imagenUrl != null && !error && nombreActual == nombreNormalizado) return

        if (forzar) {
            PokemonRepository.limpiarCache(nombreNormalizado)
            imagenUrl = null
            error = false
        }

        nombreActual = nombreNormalizado
        cargando = true
        error = false

        viewModelScope.launch {
            try {
                val pokemon = PokemonRepository.obtenerPokemon(nombreNormalizado)
                val url = pokemon.sprites?.imagen

                if (url.isNullOrBlank()) {
                    imagenUrl = null
                    error = true
                    safeLogE("PokemonImagen", "URL de imagen nula o vacía para $nombreNormalizado")
                } else {
                    imagenUrl = url
                    error = false
                    safeLogD("PokemonImagen", "URL obtenida exitosamente para $nombreNormalizado: $url")
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                imagenUrl = null
                error = true
                safeLogE("PokemonImagen", "Error al obtener Pokémon $nombreNormalizado de PokéAPI", e)
            } finally {
                cargando = false
            }
        }
    }

    fun reintentar(nombre: String) {
        cargar(nombre, forzar = true)
    }

    private fun safeLogD(tag: String, msg: String) {
        try {
            Log.d(tag, msg)
        } catch (_: Throwable) {
            // Ignorar en entorno de pruebas unitarias sin Log mockeado
        }
    }

    private fun safeLogE(tag: String, msg: String, throwable: Throwable? = null) {
        try {
            if (throwable != null) {
                Log.e(tag, msg, throwable)
            } else {
                Log.e(tag, msg)
            }
        } catch (_: Throwable) {
            // Ignorar en entorno de pruebas unitarias sin Log mockeado
        }
    }
}
