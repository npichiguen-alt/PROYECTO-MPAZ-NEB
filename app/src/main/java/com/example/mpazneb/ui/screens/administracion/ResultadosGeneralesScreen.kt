
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mpazneb.model.Actividad

@Composable
fun ResultadosGeneralesScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier
) {

    val total = actividades.size

    val lectura = actividades.count {
        it.modulo == "Lectura"
    }

    val matematica = actividades.count {
        it.modulo == "Matemática"
    }

    val activas = actividades.count {
        it.activa
    }

    val borradores = actividades.count {
        !it.activa
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Resultados generales",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Resumen de las actividades registradas.",
            style = MaterialTheme.typography.bodyMedium
        )

        ResumenCard(
            titulo = "Total de actividades",
            cantidad = total
        )

        Text(
            text = "Actividades por módulo",
            style = MaterialTheme.typography.titleMedium
        )

        ResumenCard(
            titulo = "Lectura",
            cantidad = lectura
        )

        ResumenCard(
            titulo = "Matemática",
            cantidad = matematica
        )

        Text(
            text = "Estado de las actividades",
            style = MaterialTheme.typography.titleMedium
        )

        ResumenCard(
            titulo = "Activas",
            cantidad = activas
        )

        ResumenCard(
            titulo = "Borradores",
            cantidad = borradores
        )

        HorizontalDivider()

        Text(
            text = "Resultados de aprendizaje",
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "Los resultados y avances de estudiantes " +
                    "se mostrarán cuando se integren " +
                    "los datos de los otros módulos."
        )
    }
}

@Composable
private fun ResumenCard(
    titulo: String,
    cantidad: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(titulo)

            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultadosGeneralesPreview() {

    MaterialTheme {
        ResultadosGeneralesScreen(
            actividades = listOf(
                Actividad(
                    id = 1,
                    titulo = "Resolver sumas",
                    modulo = "Matemática",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Resolver ejercicios",
                    activa = false
                ),
                Actividad(
                    id = 2,
                    titulo = "Cuento del bosque",
                    modulo = "Lectura",
                    curso = 3,
                    dificultad = "Media",
                    descripcion = "Leer un cuento",
                    activa = true
                )
            )
        )
    }
}
