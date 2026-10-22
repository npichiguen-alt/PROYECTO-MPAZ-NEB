
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NuevaActividadScreen(
    viewModel: AdministracionViewModel
) {

    var titulo by rememberSaveable {
        mutableStateOf("")
    }

    var descripcion by rememberSaveable {
        mutableStateOf("")
    }

    var modulo by rememberSaveable {
        mutableStateOf("Lectura")
    }

    var curso by rememberSaveable {
        mutableIntStateOf(1)
    }
    var dificultad by rememberSaveable {
        mutableStateOf("Básica")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Nueva actividad",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título de la actividad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Text(
            text = "Módulo",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = modulo == "Lectura",
                onClick = { modulo = "Lectura" },
                label = { Text("Lectura") }
            )

            FilterChip(
                selected = modulo == "Matemática",
                onClick = { modulo = "Matemática" },
                label = { Text("Matemática") }
            )
        }

        Text(
            text = "Curso",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (1..4).forEach { numero ->
                FilterChip(
                    selected = curso == numero,
                    onClick = { curso = numero },
                    label = { Text("${numero}°") }
                )
            }
        }
        Text(
            text = "Dificultad",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Básica", "Media", "Avanzada").forEach { opcion ->
                FilterChip(
                    selected = dificultad == opcion,
                    onClick = { dificultad = opcion },
                    label = { Text(opcion) }
                )
            }
        }
        Button(
            onClick = {
                viewModel.validarFormulario(
                    titulo = titulo,
                    descripcion = descripcion,
                    modulo = modulo,
                    curso = curso,
                    dificultad = dificultad
                )
            },
            enabled = titulo.isNotBlank() && descripcion.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ){
            Text(text = "Guardar Actividad")
        }
        if(viewModel.mensaje.isNotBlank()){
            Text(
                text = viewModel.mensaje,
                style = MaterialTheme.typography.bodyMedium

            )
    }


    }
}

@Preview(showBackground = true)
@Composable
fun NuevaActividadPreview() {

    val viewModel = remember{
        AdministracionViewModel()
    }
    MaterialTheme {
        NuevaActividadScreen(
            viewModel = viewModel
        )
    }
}
