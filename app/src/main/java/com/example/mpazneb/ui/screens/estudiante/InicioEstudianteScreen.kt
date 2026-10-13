package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun InicioEstudianteScreen(
    modifier: Modifier = Modifier,
    lecturaCompletada: Boolean = false,
    matematicaCompletada: Boolean = false,
    onAbrirLectura: () -> Unit = {},
    onAbrirMatematica: () -> Unit = {},
    onAbrirResultados: () -> Unit = {}
) {
    val totalCompletadas =
        listOf(lecturaCompletada, matematicaCompletada)
            .count { it }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Inicio de Estudiante",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Bienvenido a tus actividades educativas"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Actividades completadas: $totalCompletadas de 2",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { totalCompletadas / 2f },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Mis actividades",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA DE LECTURA

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirLectura()
                }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Lectura",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Actividades de comprensión lectora."
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (lecturaCompletada) {
                        "Estado: Completada"
                    } else {
                        "Estado: Pendiente"
                    },
                    color = if (lecturaCompletada) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (lecturaCompletada) {
                        "Presiona para revisar la actividad"
                    } else {
                        "Presiona para comenzar"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA DE MATEMÁTICA

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirMatematica()
                }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Matemática",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Actividades y ejercicios matemáticos."
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (matematicaCompletada) {
                        "Estado: Completada"
                    } else {
                        "Estado: Pendiente"
                    },
                    color = if (matematicaCompletada) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (matematicaCompletada) {
                        "Presiona para revisar la actividad"
                    } else {
                        "Presiona para comenzar"
                    },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TARJETA DE RESULTADOS

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirResultados()
                }
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Mis resultados",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Consulta tus aciertos, intentos y progreso."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Presiona para consultar",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Vista de demostración con datos ficticios",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InicioEstudiantePreview() {
    MPAZNEBTheme {
        InicioEstudianteScreen(
            lecturaCompletada = true,
            matematicaCompletada = false
        )
    }
}
