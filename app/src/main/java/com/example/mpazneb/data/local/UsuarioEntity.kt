
package com.example.mpazneb.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios_ficticios",
    indices = [
        Index(
            value = ["alias"],
            unique = true
        )
    ]
)
data class UsuarioEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val alias: String,

    val perfil: String,

    val activo: Boolean = true
)
