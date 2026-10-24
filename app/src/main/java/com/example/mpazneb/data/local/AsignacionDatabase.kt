package com.example.mpazneb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AsignacionEntity::class,
        ResultadoEntity::class
    ],
    version = 1,
    exportSchema = false
)

abstract class AsignacionDatabase : RoomDatabase() {

    abstract fun asignacionDao(): AsignacionDao
    abstract fun resultadoDao(): resultadoDao


    companion object {

        @Volatile
        private var instancia: AsignacionDatabase? = null

        fun obtenerInstancia(
            context: Context
        ): AsignacionDatabase {

            return instancia ?: synchronized(this) {

                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AsignacionDatabase::class.java,
                    "mpazneb_asignaciones.db"
                ).build().also {
                    instancia = it
                }
            }
        }
    }
}
