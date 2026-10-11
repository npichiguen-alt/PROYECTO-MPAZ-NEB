package com.example.mpazneb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun ResultadosEstudianteScreen(
    resultados: List<Resultado> = emptyList(),
    modifier: Modifier = Modifier
) {
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

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Mis resultados",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Consulta el progreso de tus actividades."
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (resultados.isEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Todavía no hay resultados registrados.",
                    modifier = Modifier.padding(20.dp)
                )
            }

        } else {

            resultados.forEach { resultado ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Asignación N.º ${resultado.asignacionId}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Aciertos: ${resultado.aciertos} " +
                                    "de ${resultado.totalPreguntas}"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Intentos: ${resultado.intentos}"
                        )

                        if (resultado.observacion.isNotEmpty()) {

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = resultado.observacion,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
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
fun ResultadosEstudiantePreview() {

    val resultadosPrueba = listOf(
        Resultado(
            id = 1,
            asignacionId = 1,
            aciertos = 1,
            totalPreguntas = 1,
            intentos = 2,
            observacion = "Actividad de Lectura (demostración)"
        ),
        Resultado(
            id = 2,
            asignacionId = 2,
            aciertos = 1,
            totalPreguntas = 1,
            intentos = 3,
            observacion = "Actividad de Matemática (demostración)"
        )
    )

    MPAZNEBTheme {
        ResultadosEstudianteScreen(
            resultados = resultadosPrueba
        )
    }
}
