
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.ui.theme.MPAZNEBTheme
import com.example.mpazneb.viewmodel.IngresoEstudianteUiState

// Colores del módulo Estudiante
private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White

@Composable
fun IngresoEstudianteScreen(
    estado: IngresoEstudianteUiState,
    onCodigoChange: (String) -> Unit,
    onIngresar: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CelestePastel)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Nombre de la aplicación
        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.titleLarge,
            color = Rosa
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Título de la pantalla
        Text(
            text = "Ingreso de Estudiante",
            style = MaterialTheme.typography.headlineMedium,
            color = AzulOscuro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bienvenido a tus actividades educativas",
            style = MaterialTheme.typography.bodyLarge,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta blanca para ingresar el código
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                Text(
                    text = "Código de acceso",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro
                )

                Text(
                    text = "Ingresa el código temporal entregado por tu asistente de aula.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTexto
                )

                // Campo de código temporal
                OutlinedTextField(
                    value = estado.codigoTemporal,
                    onValueChange = onCodigoChange,
                    label = {
                        Text("Código temporal")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Rosa,
                        unfocusedBorderColor = Azul,
                        focusedLabelColor = Rosa,
                        unfocusedLabelColor = GrisTexto,
                        focusedTextColor = AzulOscuro,
                        unfocusedTextColor = AzulOscuro,
                        cursorColor = Rosa,
                        focusedContainerColor = Blanco,
                        unfocusedContainerColor = Blanco
                    )
                )

                // Botón para ingresar
                Button(
                    onClick = onIngresar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Rosa,
                        contentColor = Blanco
                    )
                ) {

                    Text(
                        text = "Ingresar",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // Mensaje de validación
                if (estado.mensaje.isNotEmpty()) {

                    Surface(
                        color = RosaSuave,
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = estado.mensaje,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            color = AzulOscuro,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Aprende, practica y avanza",
            style = MaterialTheme.typography.bodyMedium,
            color = AzulOscuro,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Datos ficticios para uso académico",
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IngresoEstudiantePreview() {

    MPAZNEBTheme {
        IngresoEstudianteScreen(
            estado = IngresoEstudianteUiState(),
            onCodigoChange = {},
            onIngresar = {}
        )
    }
}
