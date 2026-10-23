
package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado

interface EstudianteRepository {

    // Buscar la actividad asignada usando un código
    suspend fun buscarAsignacionPorCodigo(
        codigo: String
    ): Asignacion?

    // Guardar el resultado de una actividad
    suspend fun guardarResultado(
        resultado: Resultado
    )

    // Consultar los resultados de una asignación
    suspend fun obtenerResultados(
        asignacionId: Int
    ): List<Resultado>
}
