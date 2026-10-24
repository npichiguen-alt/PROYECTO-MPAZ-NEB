
package com.example.mpazneb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        AsignacionEntity::class,
        ResultadoEntity::class,
        RespuestaEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AsignacionDatabase : RoomDatabase() {

    // Consultar y guardar asignaciones
    abstract fun asignacionDao(): AsignacionDao

    // Consultar y guardar resultados
    abstract fun resultadoDao(): resultadoDao

    // Consultar y guardar respuestas individuales
    abstract fun respuestaDao(): RespuestaDao

    companion object {

        @Volatile
        private var instancia: AsignacionDatabase? = null

        // ====================================
        // MIGRACIÓN DE VERSIÓN 1 A VERSIÓN 2
        // ====================================

        private val MIGRACION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {

                // Crear la nueva tabla de respuestas
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS respuestas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        asignacionId INTEGER NOT NULL,
                        actividadId INTEGER NOT NULL,
                        preguntaId INTEGER NOT NULL,
                        respuestaSeleccionada TEXT NOT NULL,
                        esCorrecta INTEGER NOT NULL,
                        numeroIntento INTEGER NOT NULL,
                        fechaRespuesta INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Crear un índice para buscar respuestas
                // mediante el identificador de asignación
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_respuestas_asignacionId
                    ON respuestas (asignacionId)
                    """.trimIndent()
                )
            }
        }

        // ====================================
        // OBTENER LA BASE DE DATOS
        // ====================================

        fun obtenerInstancia(
            context: Context
        ): AsignacionDatabase {

            return instancia ?: synchronized(this) {

                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AsignacionDatabase::class.java,
                    "mpazneb_asignaciones.db"
                )
                    .addMigrations(MIGRACION_1_2)
                    .build()
                    .also {
                        instancia = it
                    }
            }
        }
    }
}
