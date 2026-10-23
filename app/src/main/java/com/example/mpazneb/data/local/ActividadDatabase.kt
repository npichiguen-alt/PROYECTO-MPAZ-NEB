
package com.example.mpazneb.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ActividadEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ActividadDatabase : RoomDatabase() {

    abstract fun actividadDao(): ActividadDao

    companion object {

        @Volatile
        private var instancia: ActividadDatabase? = null

        fun obtenerInstancia(context: Context): ActividadDatabase {
            return instancia ?: synchronized(this) {

                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    ActividadDatabase::class.java,
                    "mpazneb_actividades.db"
                ).build().also {
                    instancia = it
                }
            }
        }
    }
}
