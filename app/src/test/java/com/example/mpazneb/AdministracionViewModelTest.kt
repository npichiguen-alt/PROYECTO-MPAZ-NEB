package com.example.mpazneb

import com.example.mpazneb.model.Actividad
import com.example.mpazneb.repository.administracion.ActividadRepository
import com.example.mpazneb.ui.viewmodel.AdministracionViewModel
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AdministracionViewModelTest {

    private lateinit var viewModel: AdministracionViewModel
    private lateinit var mockRepository: ActividadRepository

    @Before
    fun preparar() {
        mockRepository = mockk<ActividadRepository>(relaxed = true)
        viewModel = AdministracionViewModel(mockRepository)
    }

    @Test
    fun crearActividadRemotaConCamposVaciosSeteaMensajeDeError() {
        viewModel.crearActividadRemota(
            titulo = "",
            descripcion = "Descripcion",
            modulo = "Lectura",
            curso = 1,
            dificultad = "Básica",
            objetivo = "Objetivo",
            instrucciones = "Instrucciones"
        )

        assertEquals("Completa todos los campos obligatorios.", viewModel.mensajeRemoto)
    }

    @Test
    fun crearActividadRemotaConCursoInvalidoSeteaMensajeDeError() {
        viewModel.crearActividadRemota(
            titulo = "Titulo",
            descripcion = "Descripcion",
            modulo = "Lectura",
            curso = 5,
            dificultad = "Básica",
            objetivo = "Objetivo",
            instrucciones = "Instrucciones"
        )

        assertEquals("Selecciona un curso entre primero y cuarto básico.", viewModel.mensajeRemoto)
    }

    @Test
    fun editarActividadRemotaNoExistenteSeteaMensaje() {
        val actividad = Actividad(
            id = 999,
            titulo = "Inexistente",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Prueba",
            activa = false
        )

        viewModel.editarActividadRemota(
            actividad = actividad,
            titulo = "Nuevo Titulo",
            descripcion = "Nueva desc",
            curso = 2,
            dificultad = "Básica",
            objetivo = "Obj",
            instrucciones = "Inst"
        )

        assertEquals("Selecciona una actividad del servidor.", viewModel.mensajeRemoto)
    }

    @Test
    fun cambiarEstadoActividadRemotaNoExistenteSeteaMensaje() {
        val actividad = Actividad(
            id = 999,
            titulo = "Inexistente",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Prueba",
            activa = false
        )

        viewModel.cambiarEstadoActividadRemota(actividad)

        assertEquals("Selecciona una actividad del servidor.", viewModel.mensajeRemoto)
    }

    @Test
    fun eliminarActividadRemotaNoExistenteSeteaMensajeDeError() {
        val actividad = Actividad(
            id = 999,
            titulo = "Actividad Inexistente",
            modulo = "Lectura",
            curso = 2,
            dificultad = "Básica",
            descripcion = "Prueba",
            activa = false
        )

        viewModel.eliminarActividadRemota(actividad)

        assertEquals("Selecciona una actividad del servidor.", viewModel.mensajeRemoto)
    }
}
