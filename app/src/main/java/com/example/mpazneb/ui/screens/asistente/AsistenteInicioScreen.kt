
package com.example.mpazneb.ui.screens.asistente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.AsignacionAula

@Composable
fun AsistenteInicioScreen(
    actividades: List<ActividadAula>,
    asignaciones: List<AsignacionAula>,
    irCatalogo: () -> Unit,
    irAsignar: () -> Unit,
    irResultados: () -> Unit
) {

    val actividadesDisponibles = actividades.count {
        it.estado == "Disponible"
    }

    val totalAsignaciones = asignaciones.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // BIENVENIDA
        TituloAsistente(
            titulo = "Hola, Asistente",
            descripcion = "Apoya el aprendizaje con actividades de Lectura y Matemática."
        )

        // RESUMEN DE ACTIVIDADES
        TarjetaResumenAsistente(
            titulo = "Actividades disponibles",
            contenido = "Tienes $actividadesDisponibles actividades disponibles."
        )

        // RESUMEN DE ASIGNACIONES
        TarjetaResumenAsistente(
            titulo = "Asignaciones registradas",
            contenido = "Hay $totalAsignaciones asignaciones registradas."
        )

        // ACCESOS RAPIDOS
        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleLarge
        )

        Button(
            onClick = irCatalogo,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Explorar módulos")
        }

        Button(
            onClick = irAsignar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Asignar actividad")
        }

        BotonAsistente(
            texto = "Revisar resultados",
            onClick = irResultados
        )

    }
}
