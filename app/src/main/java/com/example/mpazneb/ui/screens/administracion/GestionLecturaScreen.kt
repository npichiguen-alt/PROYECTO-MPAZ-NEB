
package com.example.mpazneb.ui.screens.administracion

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.ui.theme.MPAZNEBTheme

@Composable
fun GestionLecturaScreen(
    actividades: List<Actividad>,
    modifier: Modifier = Modifier
) {

    // 1. GUARDAR LO QUE ESCRIBE EL USUARIO
    var busqueda by rememberSaveable {
        mutableStateOf("")
    }
    var cursoSeleccionado by rememberSaveable {
        mutableStateOf(0)
    }

    // FILTRAR POR MODULO, NOMBRE Y CURSO
    val actividadesLectura = actividades.filter { actividad ->

        actividad.modulo == "Lectura" &&

                actividad.titulo.contains(
                    busqueda.trim(),
                    ignoreCase = true
                ) &&

                (cursoSeleccionado == 0 ||
                        actividad.curso == cursoSeleccionado)
    }

    // 3. ORGANIZAR LOS ELEMENTOS DE LA PANTALLA
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Gestionar Lectura",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Consulta las actividades de Lectura.",
            style = MaterialTheme.typography.bodyMedium
        )

        // 4. CAMPO DE BUSQUEDA
        OutlinedTextField(
            value = busqueda,

            onValueChange = { textoNuevo ->
                busqueda = textoNuevo
            },

            label = {
                Text("Buscar actividad de lectura")
            },

            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        // TITULO DE LOS FILTROS
        Text(
            text = "Filtrar por curso",
            style = MaterialTheme.typography.titleMedium
        )

// BOTONES PARA SELECCIONAR CURSO
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

        // 5. MOSTRAR CANTIDAD DE RESULTADOS
        Text(
            text = "Actividades encontradas: ${actividadesLectura.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        // 6. MOSTRAR MENSAJE SI NO HAY RESULTADOS
        if (actividadesLectura.isEmpty()) {

            Text(
                text = "No se encontraron actividades de Lectura."
            )

        } else {

            // 7. MOSTRAR LAS ACTIVIDADES EN TARJETAS
            actividadesLectura.forEach { actividad ->

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

// DATOS FICTICIOS PARA LA VISTA PREVIA
@Preview(showBackground = true)
@Composable
fun GestionLecturaPreview() {

    MPAZNEBTheme {

        GestionLecturaScreen(
            actividades = listOf(

                Actividad(
                    id = 1,
                    titulo = "Cuento del bosque",
                    modulo = "Lectura",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Comprensión de un cuento corto.",
                    activa = true
                ),

                Actividad(
                    id = 2,
                    titulo = "Vocabulario divertido",
                    modulo = "Lectura",
                    curso = 3,
                    dificultad = "Media",
                    descripcion = "Aprender nuevas palabras.",
                    activa = false
                ),

                Actividad(
                    id = 3,
                    titulo = "Resolver sumas",
                    modulo = "Matemática",
                    curso = 2,
                    dificultad = "Básica",
                    descripcion = "Practicar sumas.",
                    activa = true
                )
            )
        )
    }
}
