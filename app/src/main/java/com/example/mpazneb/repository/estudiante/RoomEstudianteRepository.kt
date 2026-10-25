
package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.local.AsignacionEntity
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

    // ====================================
    // 1. BUSCAR ASIGNACIÓN POR CÓDIGO
    // ====================================

    override suspend fun buscarAsignacionPorCodigo(
        codigo: String
    ): Asignacion? {

        val codigoLimpio = codigo
            .trim()
            .uppercase(Locale.ROOT)

        // Buscar primero en Room
        var asignacion =
            asignacionRepository.buscarPorCodigo(codigoLimpio)

        // ====================================
        // 2. CÓDIGOS DE DEMOSTRACIÓN
        // ====================================

        // EST-123 conserva la prueba anterior.
        // EST-PRUEBA crea una asignación nueva
        // para probar los tres intentos.

        if (
            asignacion == null &&
            codigoLimpio in listOf(
                "EST-123",
                "EST-PRUEBA",
                "EST-FALLOS"
            )
        ) {

            val asignacionPrueba = AsignacionEntity(
                actividadId = 1,
                actividadTitulo = "Cuento del bosque",
                estudianteAlias = "Estudiante de prueba",
                codigoTemporal = codigoLimpio,
                asistenteAlias = "ASISTENTE_DEMO",
                curso = 2,
                estado = "Pendiente"
            )

            // Guardar la nueva asignación en Room
            asignacionRepository.guardarAsignacion(
                asignacionPrueba
            )

            // Recuperarla con su ID real
            asignacion =
                asignacionRepository.buscarPorCodigo(
                    codigoLimpio
                )
        }

        // No permitir códigos desconocidos
        if (asignacion == null) {
            return null
        }

        // Convertir a modelo del Estudiante
        return Asignacion(
            id = asignacion.id,
            actividadId = asignacion.actividadId,
            estudianteFicticio = asignacion.estudianteAlias,
            codigoTemporal = asignacion.codigoTemporal,
            estado = asignacion.estado
        )
    }

    // ====================================
    // 3. GUARDAR RESULTADO EN ROOM
    // ====================================

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

    // ====================================
    // 4. RECUPERAR RESULTADOS DE ROOM
    // ====================================

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
