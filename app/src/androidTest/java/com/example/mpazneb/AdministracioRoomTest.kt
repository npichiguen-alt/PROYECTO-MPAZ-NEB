
package com.example.mpazneb

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.example.mpazneb.data.local.ActividadDatabase
import com.example.mpazneb.data.local.ActividadEntity
import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.data.local.AsignacionEntity
import com.example.mpazneb.data.local.PreguntaEntity
import com.example.mpazneb.data.local.ResultadoEntity

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdministracionRoomTest {

    private lateinit var dbActividades: ActividadDatabase
    private lateinit var dbAsignaciones: AsignacionDatabase

    @Before
    fun preparar() {
        val contexto =
            InstrumentationRegistry
                .getInstrumentation()
                .targetContext

        dbActividades = Room.inMemoryDatabaseBuilder(
            contexto,
            ActividadDatabase::class.java
        ).build()

        dbAsignaciones = Room.inMemoryDatabaseBuilder(
            contexto,
            AsignacionDatabase::class.java
        ).build()
    }

    @After
    fun cerrar() {
        dbActividades.close()
        dbAsignaciones.close()
    }

    private suspend fun crearActividad(
        titulo: String
    ): Int {

        dbActividades.actividadDao().insertarActividad(
            ActividadEntity(
                titulo = titulo,
                modulo = "Lectura",
                curso = 2,
                dificultad = "Básica",
                descripcion = "Actividad de prueba",
                activa = false,
                objetivo = "Evaluar comprensión",
                instrucciones = "Lee y responde"
            )
        )

        return dbActividades.actividadDao()
            .obtenerActividades()
            .first { it.titulo == titulo }
            .id
    }

    private fun crearPregunta(
        actividadId: Int
    ) = PreguntaEntity(
        actividadId = actividadId,
        enunciado = "¿Cuánto es 2 + 2?",
        alternativaA = "3",
        alternativaB = "4",
        alternativaC = "5",
        alternativaD = "6",
        respuestaCorrecta = "B",
        retroalimentacion = "La respuesta es 4."
    )

    // ====================================
    // PRUEBA 1: NO ACTIVAR SIN PREGUNTAS
    // ====================================

    @Test
    fun actividadSinPreguntasNoSeActiva() = runBlocking {

        val id = crearActividad("Sin preguntas")

        val actualizadas =
            dbActividades.actividadDao()
                .actualizarEstadoActividad(id, true)

        assertEquals(0, actualizadas)

        val actividad = dbActividades.actividadDao()
            .obtenerActividades()
            .first { it.id == id }

        assertFalse(actividad.activa)
    }

    // ====================================
    // PRUEBA 2: ACTIVAR CON PREGUNTAS
    // ====================================

    @Test
    fun actividadConPreguntasSeActiva() = runBlocking {

        val id = crearActividad("Con preguntas")

        dbActividades.preguntaDao()
            .insertarPregunta(crearPregunta(id))

        val actualizadas =
            dbActividades.actividadDao()
                .actualizarEstadoActividad(id, true)

        assertEquals(1, actualizadas)

        val actividad = dbActividades.actividadDao()
            .obtenerActividades()
            .first { it.id == id }

        assertTrue(actividad.activa)
    }

    // ====================================
    // PRUEBA 3: DESACTIVAR ACTIVIDAD
    // ====================================

    @Test
    fun actividadSePuedeDesactivar() = runBlocking {

        val id = crearActividad("Para desactivar")

        dbActividades.preguntaDao()
            .insertarPregunta(crearPregunta(id))

        dbActividades.actividadDao()
            .actualizarEstadoActividad(id, true)

        val actualizadas =
            dbActividades.actividadDao()
                .actualizarEstadoActividad(id, false)

        assertEquals(1, actualizadas)

        val actividad = dbActividades.actividadDao()
            .obtenerActividades()
            .first { it.id == id }

        assertFalse(actividad.activa)
    }

    // ====================================
    // PRUEBA 4: EDITAR Y ELIMINAR PREGUNTA
    // ====================================

    @Test
    fun preguntaSeEditaYElimina() = runBlocking {

        val actividadId =
            crearActividad("Editar preguntas")

        val preguntaDao = dbActividades.preguntaDao()

        val idPregunta = preguntaDao.insertarPregunta(
            crearPregunta(actividadId)
        ).toInt()

        val original = preguntaDao
            .obtenerPreguntasPorActividad(actividadId)
            .first()

        assertEquals(idPregunta, original.id)

        val editada = original.copy(
            enunciado = "¿Cuánto es 3 + 3?",
            alternativaB = "6",
            retroalimentacion = "3 + 3 = 6."
        )

        val actualizadas =
            preguntaDao.actualizarPregunta(editada)

        assertEquals(1, actualizadas)

        val recuperada = preguntaDao
            .obtenerPreguntasPorActividad(actividadId)
            .first()

        assertEquals(
            "¿Cuánto es 3 + 3?",
            recuperada.enunciado
        )

        assertEquals(
            "6",
            recuperada.alternativaB
        )

        val eliminadas =
            preguntaDao.eliminarPregunta(idPregunta)

        assertEquals(1, eliminadas)

        assertTrue(
            preguntaDao
                .obtenerPreguntasPorActividad(actividadId)
                .isEmpty()
        )
    }

    // ====================================
    // PRUEBA 5: CATÁLOGO DE ACTIVIDADES
    // ====================================

    @Test
    fun catalogoSoloMuestraActividadesListas() = runBlocking {

        val idLista =
            crearActividad("Actividad disponible")

        dbActividades.preguntaDao()
            .insertarPregunta(crearPregunta(idLista))

        dbActividades.actividadDao()
            .actualizarEstadoActividad(idLista, true)

        val idBorrador =
            crearActividad("Actividad borrador")

        dbActividades.preguntaDao()
            .insertarPregunta(crearPregunta(idBorrador))

        crearActividad("Actividad sin contenido")

        val disponibles =
            dbActividades.actividadDao()
                .observarActividadesActivas()
                .first()

        assertEquals(1, disponibles.size)

        assertEquals(
            idLista,
            disponibles.first().id
        )
    }

    // ====================================
    // PRUEBA 6: ASIGNACIÓN Y RESULTADO
    // ====================================

    @Test
    fun asignacionRecuperaSuResultado() = runBlocking {

        val actividadId =
            crearActividad("Actividad evaluada")

        val asignacionId =
            dbAsignaciones.asignacionDao()
                .insertarAsignacion(
                    AsignacionEntity(
                        actividadId = actividadId,
                        actividadTitulo = "Actividad evaluada",
                        estudianteAlias = "Estudiante prueba",
                        codigoTemporal = "MAT-TEST-01",
                        asistenteAlias = "ASISTENTE_PRUEBA",
                        curso = 2
                    )
                ).toInt()

        dbAsignaciones.resultadoDao()
            .insertarResultado(
                ResultadoEntity(
                    asignacionId = asignacionId,
                    aciertos = 2,
                    totalPreguntas = 3,
                    intentos = 4,
                    observacion = "Buen progreso."
                )
            )

        val resultados =
            dbAsignaciones.resultadoDao()
                .obtenerResultados(asignacionId)

        assertEquals(1, resultados.size)
        assertEquals(2, resultados.first().aciertos)
        assertEquals(3, resultados.first().totalPreguntas)
        assertEquals(4, resultados.first().intentos)
        assertEquals(
            "Buen progreso.",
            resultados.first().observacion
        )
    }
}
