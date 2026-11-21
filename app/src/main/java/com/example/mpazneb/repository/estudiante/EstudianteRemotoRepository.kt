package com.example.mpazneb.repository.estudiante

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.model.Asignacion
import com.example.mpazneb.model.Resultado
import com.example.mpazneb.repository.asignacion.AsignacionRemotaRepository
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

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
        } catch (e: HttpException) {
            if (e.code() == 404) {
                null
            } else {
                throw e
            }
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            throw e
        }
    }

    override suspend fun guardarResultado(resultado: Resultado) {
        try {
            api.calcularResultado(resultado.asignacionId)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            throw e
        }
    }

    override suspend fun obtenerResultados(asignacionId: Int): List<Resultado> {
        return try {
            api.obtenerResultadosPorAsignacion(asignacionId)
        } catch (e: HttpException) {
            if (e.code() == 404) {
                emptyList()
            } else {
                throw e
            }
        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            throw e
        }
    }
}
