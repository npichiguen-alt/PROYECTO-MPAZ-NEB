
package com.example.mpazneb.repository.asistente

import com.example.mpazneb.model.asistente.*
import kotlinx.coroutines.flow.Flow

interface AsistenteDataSource {

    val actividades: Flow<List<ActividadAula>>

    val estudiantes: Flow<List<EstudianteAula>>

    val asignaciones: Flow<List<AsignacionAula>>

    val resultados: Flow<List<ResultadoAula>>

    suspend fun crearAsignacion(item: AsignacionAula)

    suspend fun guardarObservacion(
        resultadoId: Int,
        texto: String
    )
}
