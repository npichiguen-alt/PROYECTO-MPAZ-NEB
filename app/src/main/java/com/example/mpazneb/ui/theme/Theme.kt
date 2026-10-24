
package com.example.mpazneb.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// Colores oficiales de MPAZ NEB

private val CelestePastel = Color(0xFFE1F2FA)
private val Rosa = Color(0xFFEA3677)
private val RosaSuave = Color(0xFFFFE8F1)
private val Azul = Color(0xFF248ED8)
private val AzulSuave = Color(0xFFE7F5FE)
private val AzulOscuro = Color(0xFF242B57)
private val GrisTexto = Color(0xFF57637E)
private val Blanco = Color.White
private val RojoError = Color(0xFFB3264D)

// Paleta compartida por los tres perfiles

private val ColoresMPAZNEB = lightColorScheme(

    // Color principal de botones y controles
    primary = Rosa,
    onPrimary = Blanco,
    primaryContainer = RosaSuave,
    onPrimaryContainer = AzulOscuro,

    // Color secundario
    secondary = Azul,
    onSecondary = Blanco,
    secondaryContainer = AzulSuave,
    onSecondaryContainer = AzulOscuro,

    // Color de títulos destacados
    tertiary = AzulOscuro,
    onTertiary = Blanco,

    // Fondo general
    background = CelestePastel,
    onBackground = AzulOscuro,

    // Tarjetas y superficies
    surface = Blanco,
    onSurface = AzulOscuro,
    surfaceContainerLowest = Blanco,
    surfaceContainerLow = Blanco,
    surfaceContainer = Blanco,
    surfaceContainerHigh = AzulSuave,

    // Textos secundarios
    surfaceVariant = AzulSuave,
    onSurfaceVariant = GrisTexto,

    // Bordes
    outline = Azul,
    outlineVariant = AzulSuave,

    // Errores
    error = RojoError,
    onError = Blanco
)

// Tema global de toda la aplicación

@Composable
fun MPAZNEBTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = ColoresMPAZNEB,
        typography = Typography
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}
