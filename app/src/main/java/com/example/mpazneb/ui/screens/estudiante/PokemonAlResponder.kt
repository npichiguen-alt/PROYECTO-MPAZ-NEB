package com.example.mpazneb.ui.screens.estudiante

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.mpazneb.R
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

    var intentoImagen by remember(nombrePokemon) {
        mutableIntStateOf(0)
    }

    val nombreNormalizado = nombrePokemon.lowercase().trim()

    val localDrawableRes = remember(nombreNormalizado) {
        when (nombreNormalizado) {
            "pikachu" -> R.drawable.pikachu
            "eevee" -> R.drawable.eevee
            "bulbasaur" -> R.drawable.bulbasaur
            else -> null
        }
    }

    val contenedorColor = when (nombreNormalizado) {
        "pikachu" -> Color(0xFFE8F5E9)   // Verde suave para aciertos
        "bulbasaur" -> Color(0xFFFFF3E0) // Naranjo suave para intentos agotados
        else -> Color(0xFFFFEBEE)        // Rosado suave para reintentos
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = contenedorColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    vm.imagenUrl != null -> {
                        key(intentoImagen, vm.imagenUrl) {
                            val context = LocalContext.current
                            val imageRequest = remember(vm.imagenUrl, intentoImagen) {
                                ImageRequest.Builder(context)
                                    .data(vm.imagenUrl)
                                    .crossfade(true)
                                    .build()
                            }

                            SubcomposeAsyncImage(
                                model = imageRequest,
                                contentDescription = "Pokémon $nombrePokemon",
                                modifier = Modifier.size(100.dp),
                                loading = {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                },
                                error = {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        if (localDrawableRes != null) {
                                            Image(
                                                painter = painterResource(id = localDrawableRes),
                                                contentDescription = "Pokémon $nombrePokemon (Respaldo)",
                                                modifier = Modifier.size(64.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "Sin imagen",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }

                                        TextButton(
                                            onClick = {
                                                Log.d(
                                                    "PokemonImagen",
                                                    "Reintentando descarga de Coil para $nombrePokemon"
                                                )
                                                intentoImagen++
                                                vm.reintentar(nombrePokemon)
                                            },
                                            contentPadding = PaddingValues(0.dp),
                                            modifier = Modifier.height(24.dp)
                                        ) {
                                            Text(
                                                text = "Reintentar",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                },
                                onSuccess = {
                                    Log.d(
                                        "PokemonImagen",
                                        "Imagen de Coil cargada con éxito: $nombrePokemon"
                                    )
                                },
                                onError = { state ->
                                    Log.e(
                                        "PokemonImagen",
                                        "Error de Coil al descargar imagen para $nombrePokemon",
                                        state.result.throwable
                                    )
                                }
                            )
                        }
                    }

                    vm.cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    vm.error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (localDrawableRes != null) {
                                Image(
                                    painter = painterResource(id = localDrawableRes),
                                    contentDescription = "Pokémon $nombrePokemon (Respaldo)",
                                    modifier = Modifier.size(64.dp)
                                )
                            } else {
                                Text(
                                    text = "Sin imagen",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            TextButton(
                                onClick = {
                                    Log.d(
                                        "PokemonImagen",
                                        "Reintentando consulta Retrofit a PokéAPI para $nombrePokemon"
                                    )
                                    vm.reintentar(nombrePokemon)
                                },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(24.dp)
                            ) {
                                Text(
                                    text = "Reintentar",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nombrePokemon.replaceFirstChar {
                        it.uppercaseChar()
                    },
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
