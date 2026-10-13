
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// TARJETA PARA MOSTRAR INFORMACION
@Composable
fun TarjetaResumenAsistente(
    titulo: String,
    contenido: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = contenido,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// BOTON REUTILIZABLE
@Composable
fun BotonAsistente(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = texto)
    }
}

// TITULO DE CADA SECCION
@Composable
fun TituloAsistente(
    titulo: String,
    descripcion: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = descripcion,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
