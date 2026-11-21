package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.asignacion.AsignacionRemotaRepository

class EstudianteRemotoRepository(
    private val asignacionRepository: AsignacionRemotaRepository,
    private val api: MpazApi
) : EstudianteRepository {

    override suspend fun buscarAsignacionPorCodigo(codigo: String): Asignacion? {
        return try {
            val asignacion = asignacionRepository.obtenerAsignacionPorCodigo(codigo)
            Asignacion(
                id = asignacion.id,
                actividadId = asignacion.actividadId,
                estudianteFicticio = asignacion.estudianteAlias,
                codigoTemporal = asignacion.codigoTemporal,
                estado = asignacion.estado
            )
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun guardarResultado(resultado: Resultado) {
        try {
            api.calcularResultado(resultado.asignacionId)
        } catch (_: Exception) {
        }
    }

    override suspend fun obtenerResultados(asignacionId: Int): List<Resultado> {
        return try {
            api.obtenerResultadosPorAsignacion(asignacionId)
        } catch (_: Exception) {
            emptyList()
        }
    }
}
