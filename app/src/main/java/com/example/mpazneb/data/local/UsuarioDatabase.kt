
package com.example.mpazneb.data.local

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UsuarioEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class UsuarioDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var instancia: UsuarioDatabase? = null

        fun obtenerInstancia(
            context: Context
        ): UsuarioDatabase {

            return instancia ?: synchronized(this) {

                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    UsuarioDatabase::class.java,
                    "mpazneb_usuarios.db"
                )
                    .build()
                    .also {
                        instancia = it
                    }
            }
        }
    }
}
