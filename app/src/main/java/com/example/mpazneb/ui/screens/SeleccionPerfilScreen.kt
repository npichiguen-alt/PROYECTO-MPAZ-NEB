
package com.example.mpazneb.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.PerfilUsuario
import com.example.mpazneb.ui.theme.MPAZNEBTheme

// Colores de la pantalla de selección
private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White

@Composable
fun SeleccionPerfilScreen(
    modifier: Modifier = Modifier,
    onContinuarEstudiante: () -> Unit = {},
    onContinuarAsistente: () -> Unit = {},
    onContinuarAdministracion: () -> Unit = {}
) {

    // Guardar el perfil seleccionado
    var perfilSeleccionado by rememberSaveable {
        mutableStateOf("")
    }

    // Guardar los mensajes
    var mensaje by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CelestePastel)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Spacer(modifier = Modifier.height(12.dp))

        // ====================================
        // ENCABEZADO
        // ====================================

        Text(
            text = "MPAZ NEB",
            style = MaterialTheme.typography.headlineLarge,
            color = Rosa
        )

        Text(
            text = "Nivelación Lectura y Matemática",
            style = MaterialTheme.typography.bodyLarge,
            color = GrisTexto,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ====================================
        // TÍTULO
        // ====================================

        Text(
            text = "Selecciona tu perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = AzulOscuro,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Elige cómo deseas ingresar a la aplicación.",
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto,
            modifier = Modifier.fillMaxWidth()
        )

        // ====================================
        // LOS TRES PERFILES
        // ====================================

        PerfilUsuario.entries.forEach { perfil ->

            val seleccionado =
                perfilSeleccionado == perfil.nombreVisible

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        perfilSeleccionado = perfil.nombreVisible
                        mensaje = ""
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionado) {
                        RosaSuave
                    } else {
                        Blanco
                    }
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = 14.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RadioButton(
                        selected = seleccionado,
                        onClick = {
                            perfilSeleccionado = perfil.nombreVisible
                            mensaje = ""
                        },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Rosa,
                            unselectedColor = Azul
                        )
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = perfil.nombreVisible,
                        style = MaterialTheme.typography.titleMedium,
                        color = AzulOscuro
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ====================================
        // BOTÓN CONTINUAR
        // ====================================

        Button(
            onClick = {

                when (perfilSeleccionado) {

                    PerfilUsuario.ESTUDIANTE.nombreVisible -> {
                        onContinuarEstudiante()
                    }

                    PerfilUsuario.ASISTENTE_AULA.nombreVisible -> {
                        onContinuarAsistente()
                    }

                    PerfilUsuario.ADMINISTRACION_PEDAGOGICA.nombreVisible -> {
                        onContinuarAdministracion()
                    }

                    else -> {
                        mensaje = "Selecciona un perfil para continuar."
                    }
                }
            },
            enabled = perfilSeleccionado.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Rosa,
                contentColor = Blanco,
                disabledContainerColor = RosaSuave,
                disabledContentColor = GrisTexto
            )
        ) {

            Text(
                text = "Continuar",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ====================================
        // MENSAJES
        // ====================================

        if (mensaje.isNotEmpty()) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RosaSuave,
                shape = RoundedCornerShape(14.dp)
            ) {

                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    color = AzulOscuro,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

    }
}

// ====================================
// VISTA PREVIA
// ====================================

@Preview(showBackground = true)
@Composable
fun SeleccionPerfilPreview() {

    MPAZNEBTheme {
        SeleccionPerfilScreen(
            onContinuarEstudiante = {},
            onContinuarAsistente = {},
            onContinuarAdministracion = {}
        )
    }
}
