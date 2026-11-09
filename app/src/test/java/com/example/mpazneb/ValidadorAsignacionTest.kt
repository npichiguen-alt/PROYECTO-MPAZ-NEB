package com.example.mpazneb

import com.example.mpazneb.model.asistente.ActividadAula
import com.example.mpazneb.model.asistente.EstudianteAula
import com.example.mpazneb.model.asistente.FormularioAsignacion
import com.example.mpazneb.model.asistente.ValidadorAsignacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorAsignacionTest {

    private val actividadDisponible = ActividadAula(
        id = 1,
        titulo = "Cuento del bosque",
        modulo = "Lectura",
        curso = 2,
        dificultad = "Básica",
        descripcion = "Comprensión de lectura",
        estado = "Disponible"
    )

    private val estudianteActivo = EstudianteAula("Estudiante A", activo = true)
    private val estudianteInactivo = EstudianteAula("Estudiante D", activo = false)

    private val formularioValido = FormularioAsignacion(
        modulo = "Lectura",
        curso = 2,
        dificultad = "Básica",
        actividadId = 1,
        estudianteAlias = "Estudiante A"
    )

    private fun validar(
        formulario: FormularioAsignacion = formularioValido,
        actividades: List<ActividadAula> = listOf(actividadDisponible),
        estudiantes: List<EstudianteAula> = listOf(estudianteActivo)
    ): String? = ValidadorAsignacion.validar(formulario, actividades, estudiantes)

    @Test
    fun asignacionConDatosValidosEsAceptada() {
        assertNull(validar())
    }

    @Test
    fun estudianteInactivoEsRechazado() {
        val formulario = formularioValido.copy(estudianteAlias = "Estudiante D")
        val error = validar(formulario, estudiantes = listOf(estudianteActivo, estudianteInactivo))
        assertEquals("El estudiante no existe o no está activo.", error)
    }

    @Test
    fun estudianteInexistenteEsRechazado() {
        val formulario = formularioValido.copy(estudianteAlias = "No existe")
        assertEquals("El estudiante no existe o no está activo.", validar(formulario))
    }

    @Test
    fun actividadEnBorradorEsRechazada() {
        val actividadBorrador = actividadDisponible.copy(estado = "Borrador")
        assertEquals(
            "La actividad seleccionada no está disponible para esos filtros.",
            validar(actividades = listOf(actividadBorrador))
        )
    }

    @Test
    fun actividadNoSeleccionadaEsRechazada() {
        val formulario = formularioValido.copy(actividadId = 0)
        assertEquals("Selecciona una actividad.", validar(formulario))
    }

    @Test
    fun estudianteNoSeleccionadoEsRechazado() {
        val formulario = formularioValido.copy(estudianteAlias = "   ")
        assertEquals("Selecciona un estudiante.", validar(formulario))
    }

    @Test
    fun cursoFueraDelRangoEsRechazado() {
        val formulario = formularioValido.copy(curso = 5)
        assertEquals("Selecciona un curso entre 1.º y 4.º.", validar(formulario))
    }

    @Test
    fun cursoNoCoincidenteConActividadEsRechazado() {
        val formulario = formularioValido.copy(curso = 3)
        assertEquals(
            "La actividad seleccionada no está disponible para esos filtros.",
            validar(formulario)
        )
    }

    @Test
    fun moduloInvalidoEsRechazado() {
        val formulario = formularioValido.copy(modulo = "Ciencias")
        assertEquals("Módulo inválido.", validar(formulario))
    }

    @Test
    fun dificultadInvalidaEsRechazada() {
        val formulario = formularioValido.copy(dificultad = "Extrema")
        assertEquals("Selecciona una dificultad válida.", validar(formulario))
    }

    @Test
    fun observacionVaciaEsRechazada() {
        assertEquals("Escribe una observación.", ValidadorAsignacion.validarObservacion("   "))
    }

    @Test
    fun observacionDeMasDe250CaracteresEsRechazada() {
        val observacion = "a".repeat(251)
        assertEquals(
            "La observación no puede superar 250 caracteres.",
            ValidadorAsignacion.validarObservacion(observacion)
        )
    }

    @Test
    fun observacionDe250CaracteresEsAceptada() {
        assertNull(ValidadorAsignacion.validarObservacion("a".repeat(250)))
    }
}
