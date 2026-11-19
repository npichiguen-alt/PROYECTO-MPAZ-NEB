
package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.ResultadoEntity
import com.example.mpazneb.data.local.resultadoDao
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.asignacion.AsignacionRepository
import java.util.Locale

class RoomEstudianteRepository(
    private val asignacionRepository: AsignacionRepository,
    private val resultadoDao: resultadoDao
) : EstudianteRepository {

    // 1. BUSCAR ASIGNACIÓN REAL POR CÓDIGO

    override suspend fun buscarAsignacionPorCodigo(
        codigo: String
    ): Asignacion? {

        val codigoLimpio = codigo
            .trim()
            .uppercase(Locale.ROOT)

        if (codigoLimpio.isBlank()) {
            return null
        }

        val asignacion = asignacionRepository
            .buscarPorCodigo(codigoLimpio)
            ?: return null

        return Asignacion(
            id = asignacion.id,
            actividadId = asignacion.actividadId,
            estudianteFicticio = asignacion.estudianteAlias,
            codigoTemporal = asignacion.codigoTemporal,
            estado = asignacion.estado
        )
    }

    // 2. GUARDAR RESULTADO EN ROOM

    override suspend fun guardarResultado(
        resultado: Resultado
    ) {

        val resultadoEntity = ResultadoEntity(
            asignacionId = resultado.asignacionId,
            aciertos = resultado.aciertos,
            totalPreguntas = resultado.totalPreguntas,
            intentos = resultado.intentos,
            observacion = resultado.observacion
        )

        resultadoDao.insertarResultado(
            resultadoEntity
        )
    }

    // 3. RECUPERAR RESULTADOS DE ROOM

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
