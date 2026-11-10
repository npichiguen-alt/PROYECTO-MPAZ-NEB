package com.example.mpazneb.viewmodel

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

    fun cargar(nombre: String) {

        if (cargando || imagenUrl != null) return

        viewModelScope.launch {

            cargando = true
            error = false

            try {
                val pokemon =
                    PokemonRepository.obtenerPokemon(nombre)

                imagenUrl = pokemon.sprites.imagen

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                error = true

            } finally {
                cargando = false
            }
        }
    }
}
