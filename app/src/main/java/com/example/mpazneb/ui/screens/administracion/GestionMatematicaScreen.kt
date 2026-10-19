
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.ui.theme.MPAZNEBTheme

// PANTALLA PARA GESTIONAR MATEMÁTICA
@Composable
fun GestionMatematicaScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier
) {

    // 1. RECORDAR EL TEXTO DEL BUSCADOR
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }

    // 2. RECORDAR EL CURSO ELEGIDO
    // El valor 0 significa "Todos"
    var cursoSeleccionado by rememberSaveable {
        mutableStateOf(0)
    }

    // 3. FILTRAR LAS ACTIVIDADES
    val actividadesMatematica = actividades.filter { actividad ->

        actividad.modulo == "Matemática" &&

                actividad.titulo.contains(
                    busqueda.trim(),
                    ignoreCase = true
                ) &&

                (cursoSeleccionado == 0 ||
                        actividad.curso == cursoSeleccionado)
    }

    // 4. CONTENIDO DE LA PANTALLA
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Gestionar Matemática",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Consulta las actividades de Matemática.",
            style = MaterialTheme.typography.bodyMedium
        )

        // 5. BUSCADOR
        OutlinedTextField(
            value = busqueda,

            onValueChange = { textoNuevo ->
                busqueda = textoNuevo
            },

            label = {
                Text("Buscar actividad de matemática")
            },

            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // 6. FILTROS POR CURSO
        Text(
            text = "Filtrar por curso",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.horizontalScroll(
                rememberScrollState()
            ),

            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            (0..4).forEach { curso ->

                FilterChip(
                    selected = cursoSeleccionado == curso,

                    onClick = {
                        cursoSeleccionado = curso
                    },

                    label = {
                        Text(
                            if (curso == 0) {
                                "Todos"
                            } else {
                                "$curso.º básico"
                            }
                        )
                    }
                )
            }
        }

        // 7. CONTADOR DE ACTIVIDADES
        Text(
            text = "Actividades encontradas: ${actividadesMatematica.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        // 8. MOSTRAR ACTIVIDADES
        if (actividadesMatematica.isEmpty()) {

            Text(
                text = "No se encontraron actividades de Matemática."
            )

        } else {

            actividadesMatematica.forEach { actividad ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),

                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = actividad.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = actividad.descripcion
                        )

                        Text(
                            text = "${actividad.curso}° básico"
                        )

                        Text(
                            text = "Dificultad: ${actividad.dificultad}"
                        )

                        Text(
                            text = if (actividad.activa) {
                                "Estado: Activa"
                            } else {
                                "Estado: Borrador"
                            }
                        )
                    }
                }
            }
        }
    }
}

// VISTA PREVIA CON DATOS FICTICIOS
@Preview(showBackground = true)
@Composable
fun GestionMatematicaPreview() {

    MPAZNEBTheme {

        GestionMatematicaScreen(
            actividades = listOf(

                Actividad(
                    id = 1,
                    titulo = "Resolver sumas",
                    modulo = "Matemática",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Practicar sumas de números naturales.",
                    activa = true
                ),

                Actividad(
                    id = 2,
                    titulo = "Problemas cotidianos",
                    modulo = "Matemática",
                    curso = 3,
                    dificultad = "Media",
                    descripcion = "Resolver problemas usando sumas y restas.",
                    activa = true
                ),

                Actividad(
                    id = 3,
                    titulo = "Patrones y secuencias",
                    modulo = "Matemática",
                    curso = 4,
                    dificultad = "Avanzada",
                    descripcion = "Reconocer secuencias numéricas.",
                    activa = false
                ),

                // Esta actividad NO debe mostrarse
                Actividad(
                    id = 4,
                    titulo = "Cuento del bosque",
                    modulo = "Lectura",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Comprensión de lectura.",
                    activa = true
                )
            )
        )
    }
}

