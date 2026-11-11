package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import coil3.compose.AsyncImage

import com.example.mpazneb.viewmodel.PokemonViewModel

@Composable
fun PokemonAlResponder(
    nombrePokemon: String,
    mensaje: String
) {

    val vm: PokemonViewModel = viewModel(
        key = "pokemon-$nombrePokemon"
    )

    LaunchedEffect(nombrePokemon) {
        vm.cargar(nombrePokemon)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            if (vm.imagenUrl != null) {

                AsyncImage(
                    model = vm.imagenUrl,
                    contentDescription = "Pokémon $nombrePokemon",
                    modifier = Modifier.size(100.dp)
                )

            } else if (vm.cargando) {

                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )

            } else if (vm.error) {

                Column(
                    modifier = Modifier.width(100.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Text(
                        text = "📡",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Sin imagen",
                        style = MaterialTheme.typography.bodySmall
                    )

                    TextButton(
                        onClick = {
                            vm.cargar(nombrePokemon)
                        }
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = nombrePokemon.replaceFirstChar {
                        it.uppercaseChar()
                    },
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Text(
                    text = mensaje,
                    style =
                        MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
