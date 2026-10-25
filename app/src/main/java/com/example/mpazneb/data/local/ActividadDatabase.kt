
package com.example.mpazneb.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        ActividadEntity::class,
        PreguntaEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ActividadDatabase : RoomDatabase() {

    // DAO de actividades
    abstract fun actividadDao(): ActividadDao

    // NUEVO: DAO de preguntas
    abstract fun preguntaDao(): PreguntaDao

    companion object {

        @Volatile
        private var instancia: ActividadDatabase? = null

        // ====================================
        // MIGRACIÓN 1 A 2
        // Conserva las actividades existentes
        // ====================================

        private val MIGRACION_1_2 = object : Migration(1, 2) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {

                db.execSQL(
                    """
                    ALTER TABLE actividades
                    ADD COLUMN objetivo TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    ALTER TABLE actividades
                    ADD COLUMN instrucciones TEXT NOT NULL DEFAULT ''
                    """.trimIndent()
                )
            }
        }

        // ====================================
        // NUEVO: MIGRACIÓN 2 A 3
        // Crea la tabla de preguntas
        // SIN borrar las actividades
        // ====================================

        private val MIGRACION_2_3 = object : Migration(2, 3) {

            override fun migrate(
                db: SupportSQLiteDatabase
            ) {

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS preguntas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        actividadId INTEGER NOT NULL,
                        enunciado TEXT NOT NULL,
                        alternativaA TEXT NOT NULL,
                        alternativaB TEXT NOT NULL,
                        alternativaC TEXT NOT NULL,
                        alternativaD TEXT NOT NULL,
                        respuestaCorrecta TEXT NOT NULL,
                        retroalimentacion TEXT NOT NULL,
                        FOREIGN KEY (actividadId)
                            REFERENCES actividades(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS
                    index_preguntas_actividadId
                    ON preguntas (actividadId)
                    """.trimIndent()
                )
            }
        }

        // ====================================
        // OBTENER INSTANCIA DE ROOM
        // ====================================

        fun obtenerInstancia(
            context: Context
        ): ActividadDatabase {

            return instancia ?: synchronized(this) {

                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    ActividadDatabase::class.java,
                    "mpazneb_actividades.db"
                )
                    .addMigrations(
                        MIGRACION_1_2,
                        MIGRACION_2_3
                    )
                    .build()
                    .also {
                        instancia = it
                    }
            }
        }
    }
}
