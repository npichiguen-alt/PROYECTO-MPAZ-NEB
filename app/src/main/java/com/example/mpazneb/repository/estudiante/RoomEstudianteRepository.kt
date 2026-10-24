package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.resultadoDao
import com.example.mpazneb.data.local.ResultadoEntity
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.asignacion.AsignacionRepository

class RoomEstudianteRepository(
    private val asignacionRepository: AsignacionRepository,
    private val resultadoDao: resultadoDao
) : EstudianteRepository {

    // Buscar el código de la actividad asignada
    override suspend fun buscarAsignacionPorCodigo(
        codigo: String
    ): Asignacion? {

        val asignacion =
            asignacionRepository.buscarPorCodigo(codigo)
                ?: return null

        return Asignacion(
            id = asignacion.id,
            actividadId = asignacion.actividadId,
            estudianteFicticio = asignacion.estudianteAlias,
            codigoTemporal = asignacion.codigoTemporal,
            estado = asignacion.estado
        )
    }

    // Guardar un resultado en Room
    override suspend fun guardarResultado(
        resultado: Resultado
    ) {
        resultadoDao.insertarResultado(
            ResultadoEntity(
                asignacionId = resultado.asignacionId,
                aciertos = resultado.aciertos,
                totalPreguntas = resultado.totalPreguntas,
                intentos = resultado.intentos,
                observacion = resultado.observacion
            )
        )
    }

    // Recuperar resultados almacenados
    override suspend fun obtenerResultados(
        asignacionId: Int
    ): List<Resultado> {

        return resultadoDao
            .obtenerResultados(asignacionId)
            .map { registro ->
                Resultado(
                    id = registro.id,
                    asignacionId = registro.asignacionId,
                    aciertos = registro.aciertos,
                    totalPreguntas = registro.totalPreguntas,
                    intentos = registro.intentos,
                    observacion = registro.observacion
                )
            }
    }
}
