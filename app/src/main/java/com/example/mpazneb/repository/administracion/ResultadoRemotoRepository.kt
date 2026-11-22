package com.example.mpazneb.repository.administracion

import com.example.mpazneb.data.remote.MpazApi
import com.example.mpazneb.data.remote.RetrofitClient
import com.example.mpazneb.model.Actividad
import com.example.mpazneb.model.AsignacionRemota
import com.example.mpazneb.model.Resultado

data class DetalleResultadoRemoto(
    val asignacion: AsignacionRemota,
    val resultado: Resultado,
    val actividad: Actividad?
)

data class ResumenPedagogicoRemoto(
    val actividades: List<Actividad> = emptyList(),
    val asignaciones: List<AsignacionRemota> = emptyList(),
    val detalles: List<DetalleResultadoRemoto> = emptyList()
) {

    val totalActividades: Int
        get() = actividades.size

    val actividadesActivas: Int
        get() = actividades.count { it.activa }

    val actividadesBorrador: Int
        get() = actividades.count { !it.activa }

    val totalAsignaciones: Int
        get() = asignaciones.size

    val asignacionesConResultado: Int
        get() = detalles.size

    val asignacionesSinResultado: Int
        get() = totalAsignaciones - asignacionesConResultado

    val totalAciertos: Int
        get() = detalles.sumOf {
            it.resultado.aciertos
        }

    val totalPreguntas: Int
        get() = detalles.sumOf {
            it.resultado.totalPreguntas
        }

    val totalIntentos: Int
        get() = detalles.sumOf {
            it.resultado.intentos
        }

    val porcentajeGeneral: Float
        get() = if (totalPreguntas > 0) {
            (
                totalAciertos.toFloat() /
                    totalPreguntas.toFloat()
            ).coerceIn(0f, 1f)
        } else {
            0f
        }
}

class ResultadoRemotoRepository(
    private val api: MpazApi = RetrofitClient.api
) {

    suspend fun obtenerResumen(): ResumenPedagogicoRemoto {

        val actividades =
            api.obtenerActividades()

        val asignaciones =
            api.obtenerAsignaciones().distinctBy { it.id }

        val actividadesPorId =
            actividades.associateBy { it.id }

        val detalles = mutableListOf<DetalleResultadoRemoto>()

        for (asignacion in asignaciones) {

            val resultados =
                api.obtenerResultadosPorAsignacion(
                    asignacion.id
                )

            val resultadoFinal = resultados
                .filter {
                    it.asignacionId == asignacion.id
                }
                .maxWithOrNull(
                    compareBy<Resultado> {
                        it.fechaRegistro
                    }.thenBy {
                        it.id
                    }
                )

            if (resultadoFinal != null) {

                detalles.add(
                    DetalleResultadoRemoto(
                        asignacion = asignacion,
                        resultado = resultadoFinal,
                        actividad = actividadesPorId[
                            asignacion.actividadId
                        ]
                    )
                )
            }
        }

        return ResumenPedagogicoRemoto(
            actividades = actividades,
            asignaciones = asignaciones,
            detalles = detalles.sortedByDescending {
                it.resultado.fechaRegistro
            }
        )
    }
}