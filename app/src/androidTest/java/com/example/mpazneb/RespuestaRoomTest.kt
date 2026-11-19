
package com.example.mpazneb

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.example.mpazneb.data.local.AsignacionDatabase
import com.example.mpazneb.data.local.AsignacionEntity

import kotlinx.coroutines.runBlocking

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RespuestaRoomTest {

    private lateinit var database: AsignacionDatabase

    @Before
    fun preparar() {

        val contexto = InstrumentationRegistry
            .getInstrumentation()
            .targetContext

        database = Room.inMemoryDatabaseBuilder(
            contexto,
            AsignacionDatabase::class.java
        ).build()
    }

    @After
    fun cerrar() {
        database.close()
    }

    @Test
    fun tresErroresGuardanUnResultadoYBloqueanCuartoIntento() =
        runBlocking {

            // Crear una asignación solo en memoria.

            val asignacionId = database
                .asignacionDao()
                .insertarAsignacion(
                    AsignacionEntity(
                        actividadId = 100,
                        actividadTitulo = "Actividad de prueba",
                        estudianteAlias = "Estudiante test",
                        codigoTemporal = "TEST-ROOM",
                        asistenteAlias = "ASISTENTE_TEST",
                        curso = 2
                    )
                ).toInt()

            val dao = database.respuestaDao()

            // Registrar tres respuestas incorrectas.

            repeat(3) { indice ->

                val guardada =
                    dao.guardarRespuestaYResultado(
                        asignacionId = asignacionId,
                        actividadId = 100,
                        preguntaId = 200,
                        respuestaSeleccionada = "A",
                        esCorrecta = false,
                        preguntasIds = listOf(200)
                    )

                assertTrue(guardada)

                val respuestas =
                    dao.obtenerRespuestas(asignacionId)

                assertEquals(
                    indice + 1,
                    respuestas.size
                )
            }

            // Intentar una cuarta respuesta.

            val cuartoIntento =
                dao.guardarRespuestaYResultado(
                    asignacionId = asignacionId,
                    actividadId = 100,
                    preguntaId = 200,
                    respuestaSeleccionada = "B",
                    esCorrecta = true,
                    preguntasIds = listOf(200)
                )

            // El cuarto intento debe rechazarse.

            assertFalse(cuartoIntento)

            val respuestasFinales =
                dao.obtenerRespuestas(asignacionId)

            assertEquals(3, respuestasFinales.size)

            // Debe existir un solo resultado final.

            val resultados =
                database.resultadoDao()
                    .obtenerResultados(asignacionId)

            assertEquals(1, resultados.size)

            assertEquals(
                0,
                resultados.first().aciertos
            )

            assertEquals(
                1,
                resultados.first().totalPreguntas
            )

            assertEquals(
                3,
                resultados.first().intentos
            )
        }


    @Test
    fun actividadConVariasPreguntasGuardaUnSoloResultado() =
        runBlocking {

            val asignacionId = database
                .asignacionDao()
                .insertarAsignacion(
                    AsignacionEntity(
                        actividadId = 101,
                        actividadTitulo = "Matemática",
                        estudianteAlias = "Estudiante de prueba",
                        codigoTemporal = "TEST-VARIAS",
                        asistenteAlias = "ASISTENTE_TEST",
                        curso = 2
                    )
                ).toInt()

            val dao = database.respuestaDao()

            val preguntasIds = listOf(201, 202)

            // PRIMERA PREGUNTA: RESPUESTA CORRECTA

            val primeraRespuesta =
                dao.guardarRespuestaYResultado(
                    asignacionId = asignacionId,
                    actividadId = 101,
                    preguntaId = 201,
                    respuestaSeleccionada = "B",
                    esCorrecta = true,
                    preguntasIds = preguntasIds
                )

            assertTrue(primeraRespuesta)

            // Todavía no debe existir resultado final.

            val resultadosParciales =
                database.resultadoDao()
                    .obtenerResultados(asignacionId)

            assertTrue(resultadosParciales.isEmpty())

            // SEGUNDA PREGUNTA: TRES ERRORES

            repeat(3) {

                val respuestaGuardada =
                    dao.guardarRespuestaYResultado(
                        asignacionId = asignacionId,
                        actividadId = 101,
                        preguntaId = 202,
                        respuestaSeleccionada = "A",
                        esCorrecta = false,
                        preguntasIds = preguntasIds
                    )

                assertTrue(respuestaGuardada)
            }

            // Ahora ambas preguntas están finalizadas.

            val resultadosFinales =
                database.resultadoDao()
                    .obtenerResultados(asignacionId)

            assertEquals(1, resultadosFinales.size)

            val resultado = resultadosFinales.first()

            // Una correcta de dos preguntas.

            assertEquals(1, resultado.aciertos)
            assertEquals(2, resultado.totalPreguntas)

            // Un intento en la primera pregunta
            // y tres intentos en la segunda.

            assertEquals(4, resultado.intentos)

            // NO PERMITIR MÁS RESPUESTAS.

            val intentoExtra =
                dao.guardarRespuestaYResultado(
                    asignacionId = asignacionId,
                    actividadId = 101,
                    preguntaId = 201,
                    respuestaSeleccionada = "B",
                    esCorrecta = true,
                    preguntasIds = preguntasIds
                )

            assertFalse(intentoExtra)

            // Verificar que no existan duplicados.

            val resultadosDefinitivos =
                database.resultadoDao()
                    .obtenerResultados(asignacionId)

            assertEquals(1, resultadosDefinitivos.size)

            // El historial conserva cuatro intentos.

            val respuestas =
                dao.obtenerRespuestas(asignacionId)

            assertEquals(4, respuestas.size)
        }

}