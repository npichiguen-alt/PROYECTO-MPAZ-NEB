package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado

class DemoEstudianteRepository : EstudianteRepository {

    // Asignación ficticia para probar el ingreso
    private val asignaciones = listOf(
        Asignacion(
            id = 1,
            actividadId = 1,
            estudianteFicticio = "Estudiante de prueba",
            codigoTemporal = "EST-123",
            estado = "Pendiente"
        )
    )

    // Resultados guardados temporalmente en memoria
    private val resultados = mutableListOf<Resultado>()

    override suspend fun buscarAsignacionPorCodigo(
        codigo: String
    ): Asignacion? {
        return asignaciones.firstOrNull {
            it.codigoTemporal == codigo.trim()
        }
    }

    override suspend fun guardarResultado(
        resultado: Resultado
    ) {
        resultados.add(resultado)
    }

    override suspend fun obtenerResultados(
        asignacionId: Int
    ): List<Resultado> {
        return resultados.filter {
            it.asignacionId == asignacionId
        }
    }
}
