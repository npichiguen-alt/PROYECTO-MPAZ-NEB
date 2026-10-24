
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
    mostrarLectura: Boolean = true,
    mostrarMatematica: Boolean = true,
    onAbrirLectura: () -> Unit = {},
    onAbrirMatematica: () -> Unit = {},
    onAbrirResultados: () -> Unit = {}
) {

    // Contar solamente las actividades asignadas
    val totalAsignadas =
        (if (mostrarLectura) 1 else 0) +
                (if (mostrarMatematica) 1 else 0)

    val totalCompletadas =
        (if (mostrarLectura && lecturaCompletada) 1 else 0) +
                (if (mostrarMatematica && matematicaCompletada) 1 else 0)

    val progreso = if (totalAsignadas > 0) {
        totalCompletadas.toFloat() / totalAsignadas
    } else {
        0f
    }

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
            text = "Actividades completadas: " +
                    "$totalCompletadas de $totalAsignadas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Mis actividades",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ====================================
        // ACTIVIDAD DE LECTURA
        // ====================================

        if (mostrarLectura) {

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
                        color = MaterialTheme.colorScheme.primary,
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
        }

        // ====================================
        // ACTIVIDAD DE MATEMÁTICA
        // ====================================

        if (mostrarMatematica) {

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
                        color = MaterialTheme.colorScheme.primary,
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
        }

        // Mensaje cuando no hay actividades asignadas
        if (totalAsignadas == 0) {

            Text(
                text = "No tienes actividades asignadas."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ====================================
        // MIS RESULTADOS
        // ====================================

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
            text = "Datos ficticios para uso académico",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InicioEstudiantePreview() {

    MPAZNEBTheme {

        InicioEstudianteScreen(
            lecturaCompletada = false,
            matematicaCompletada = false,
            mostrarLectura = true,
            mostrarMatematica = false
        )
    }
}
