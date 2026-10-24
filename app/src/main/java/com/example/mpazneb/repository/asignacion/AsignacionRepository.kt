
package com.example.mpazneb.repository.asignacion

import com.example.mpazneb.data.local.AsignacionDao
import com.example.mpazneb.data.local.AsignacionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Locale

class AsignacionRepository(
    private val dao: AsignacionDao
) {

    // Guardar una nueva asignación
    suspend fun guardarAsignacion(
        asignacion: AsignacionEntity
    ): Long {
        return dao.insertarAsignacion(asignacion)
    }

    // Buscar asignación mediante código temporal
    suspend fun buscarPorCodigo(
        codigo: String
    ): AsignacionEntity? {

        val codigoLimpio = codigo
            .trim()
            .uppercase(Locale.ROOT)

        return dao.buscarPorCodigo(codigoLimpio)
    }

    // Consultar las asignaciones almacenadas
    fun observarAsignaciones(): Flow<List<AsignacionEntity>> {
        return dao.observarAsignaciones()
    }
}
