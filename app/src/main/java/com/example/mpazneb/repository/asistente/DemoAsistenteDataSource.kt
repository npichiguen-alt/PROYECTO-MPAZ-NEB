package com.example.mpazneb.repository.asistente

import com.example.mpazneb.model.asistente.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DemoAsistenteDataSource : AsistenteDataSource {

    // ACTIVIDADES FICTICIAS
    private val _actividades = MutableStateFlow(
        listOf(
            ActividadAula(
                1,
                "Cuento del bosque",
                "Lectura",
                2,
                "Básica",
                "Comprensión de lectura con texto breve.",
                "Disponible"
            ),
            ActividadAula(
                2,
                "Resolver sumas",
                "Matemática",
                2,
                "Básica",
                "Sumas de números naturales.",
                "Disponible"
            ),
            ActividadAula(
                3,
                "Vocabulario divertido",
                "Lectura",
                3,
                "Media",
                "Identificar sinónimos en contextos simples.",
                "Disponible"
            ),
            ActividadAula(
                4,
                "Resolver problemas",
                "Matemática",
                3,
                "Media",
                "Problemas matemáticos sencillos.",
                "Disponible"
            ),
            ActividadAula(
                5,
                "Borrador de lectura",
                "Lectura",
                2,
                "Básica",
                "Actividad pendiente de aprobación.",
                "Borrador"
            )
        )
    )

    override val actividades = _actividades.asStateFlow()

    // ESTUDIANTES FICTICIOS
    private val _estudiantes = MutableStateFlow(
        listOf(
            EstudianteAula("Estudiante A"),
            EstudianteAula("Estudiante B"),
            EstudianteAula("Estudiante C"),
            EstudianteAula("Estudiante D", false)
        )
    )

    override val estudiantes = _estudiantes.asStateFlow()

    // ASIGNACIONES DE PRUEBA
    private val _asignaciones = MutableStateFlow(
        listOf(
            AsignacionAula(
                101,
                1,
                "Cuento del bosque",
                "Estudiante A",
                "DEMO-204",
                "ASISTENTE_DEMO",
                2,
                "Completada"
            )
        )
    )

    override val asignaciones = _asignaciones.asStateFlow()

    // RESULTADOS DE PRUEBA
    private val _resultados = MutableStateFlow(
        listOf(
            ResultadoAula(
                50,
                101,
                3,
                4,
                2,
                "Necesita seguir practicando comprensión de lectura."
            )
        )
    )

    override val resultados = _resultados.asStateFlow()

    // CREA UNA NUEVA ASIGNACIÓN
    override suspend fun crearAsignacion(
        item: AsignacionAula
    ) {

        require(
            _asignaciones.value.none {
                it.codigoTemporal == item.codigoTemporal
            }
        ) {
            "El código ya existe; intenta nuevamente."
        }

        val nuevoId =
            (_asignaciones.value.maxOfOrNull { it.id } ?: 0) + 1

        _asignaciones.value =
            listOf(item.copy(id = nuevoId)) + _asignaciones.value
    }

    // GUARDA UNA OBSERVACIÓN PEDAGÓGICA
    override suspend fun guardarObservacion(
        resultadoId: Int,
        texto: String
    ) {

        require(
            _resultados.value.any {
                it.id == resultadoId
            }
        ) {
            "Resultado inexistente."
        }

        _resultados.value = _resultados.value.map {
            if (it.id == resultadoId) {
                it.copy(
                    observacion = texto.trim()
                )
            } else {
                it
            }
        }
    }
}
