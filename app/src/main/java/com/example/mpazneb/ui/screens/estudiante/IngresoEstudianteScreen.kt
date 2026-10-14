package com.example.mpazneb.ui.screens.estudiante

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
import com.example.mpazneb.viewmodel.IngresoEstudianteUiState

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
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Ingreso de Estudiante",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ingresa el código temporal entregado " +
                    "por tu asistente de aula."
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = estado.codigoTemporal,
            onValueChange = onCodigoChange,
            label = {
                Text("Código temporal")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onIngresar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ingresar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.mensaje.isNotEmpty()) {
            Text(
                text = estado.mensaje,
                color = MaterialTheme.colorScheme.primary
            )
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
fun IngresoEstudiantePreview() {
    MPAZNEBTheme {
        IngresoEstudianteScreen(
            estado = IngresoEstudianteUiState(),
            onCodigoChange = {},
            onIngresar = {}
        )
    }
}
