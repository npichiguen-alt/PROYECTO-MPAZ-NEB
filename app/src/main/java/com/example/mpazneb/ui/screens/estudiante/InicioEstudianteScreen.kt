
package com.example.mpazneb.ui.screens.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.ui.theme.MPAZNEBTheme

// Colores del módulo Estudiante
private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulSuave = Color(0xFFE7F5FE)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White

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
            .background(CelestePastel)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // ENCABEZADO
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = "MPAZ NEB",
                style = MaterialTheme.typography.titleMedium,
                color = Rosa
            )

            Text(
                text = "¡Hola, estudiante!",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulOscuro
            )

            Text(
                text = "Continúa aprendiendo a tu ritmo.",
                style = MaterialTheme.typography.bodyLarge,
                color = GrisTexto
            )
        }

        // TARJETA DE PROGRESO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = Blanco
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Mi progreso",
                    style = MaterialTheme.typography.titleLarge,
                    color = AzulOscuro
                )

                Text(
                    text = "$totalCompletadas de $totalAsignadas actividades completadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = GrisTexto
                )

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = Rosa,
                    trackColor = RosaSuave
                )

                Text(
                    text = when {
                        totalAsignadas == 0 ->
                            "Pronto podrás comenzar tus actividades."

                        totalCompletadas == totalAsignadas ->
                            "¡Muy bien! Completaste tus actividades."

                        else ->
                            "Sigue practicando y avanzando."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Rosa
                )
            }
        }

        // ACTIVIDADES
        Text(
            text = "Mis actividades",
            style = MaterialTheme.typography.titleLarge,
            color = AzulOscuro
        )

        // LECTURA
        if (mostrarLectura) {

            TarjetaActividadEstudiante(
                titulo = "Lectura",
                descripcion = "Lee historias y desarrolla tu comprensión.",
                completada = lecturaCompletada,
                colorPrincipal = Rosa,
                colorEtiqueta = RosaSuave,
                onClick = onAbrirLectura
            )
        }

        // MATEMÁTICA
        if (mostrarMatematica) {

            TarjetaActividadEstudiante(
                titulo = "Matemática",
                descripcion = "Practica operaciones y resuelve ejercicios.",
                completada = matematicaCompletada,
                colorPrincipal = Azul,
                colorEtiqueta = AzulSuave,
                onClick = onAbrirMatematica
            )
        }

        // SIN ACTIVIDADES
        if (totalAsignadas == 0) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Blanco
                )
            ) {

                Text(
                    text = "No tienes actividades asignadas.",
                    modifier = Modifier.padding(20.dp),
                    color = GrisTexto
                )
            }
        }

        // MIS RESULTADOS
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onAbrirResultados()
                },
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = AzulOscuro
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Mis resultados",
                    style = MaterialTheme.typography.titleLarge,
                    color = Blanco
                )

                Text(
                    text = "Consulta tus aciertos, intentos y progreso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFEDF1FA)
                )

                Surface(
                    color = Rosa,
                    shape = RoundedCornerShape(12.dp)
                ) {

                    Text(
                        text = "Ver resultados",
                        modifier = Modifier.padding(
                            horizontal = 18.dp,
                            vertical = 10.dp
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        color = Blanco
                    )
                }
            }
        }

        // PIE DE PÁGINA
        Text(
            text = "Datos ficticios para uso académico",
            style = MaterialTheme.typography.bodySmall,
            color = GrisTexto
        )
    }
}

// TARJETA PARA LECTURA Y MATEMÁTICA
@Composable
private fun TarjetaActividadEstudiante(
    titulo: String,
    descripcion: String,
    completada: Boolean,
    colorPrincipal: Color,
    colorEtiqueta: Color,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Blanco
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(58.dp)
                        .background(
                            colorPrincipal,
                            RoundedCornerShape(8.dp)
                        )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleLarge,
                        color = AzulOscuro
                    )

                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GrisTexto
                    )
                }
            }

            Surface(
                color = colorEtiqueta,
                shape = RoundedCornerShape(50.dp)
            ) {

                Text(
                    text = if (completada) {
                        "Completada"
                    } else {
                        "Pendiente"
                    },
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 7.dp
                    ),
                    color = AzulOscuro,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = if (completada) {
                    "Revisar actividad"
                } else {
                    "Comenzar actividad"
                },
                style = MaterialTheme.typography.titleSmall,
                color = colorPrincipal
            )
        }
    }
}

// VISTA PREVIA
@Preview(showBackground = true)
@Composable
fun InicioEstudiantePreview() {

    MPAZNEBTheme {
        InicioEstudianteScreen(
            lecturaCompletada = true,
            matematicaCompletada = false,
            mostrarLectura = true,
            mostrarMatematica = false
        )
    }
}
