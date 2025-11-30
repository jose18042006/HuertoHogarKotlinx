package com.huertohogar.huertohogarkotlinx.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.huertohogar.huertohogarkotlinx.data.model.FormModel

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val nombre: String,
    val email: String,
    val edad: Int,
    val aceptaTerminos: Boolean,
    val comentario: String?,
    val profileImageUri: String? = null // <- AÑADIDO
)

fun FormModel.toEntity(): UserEntity {
    return UserEntity(
        id = 1,
        nombre = this.nombre,
        email = this.email,
        edad = this.edad,
        aceptaTerminos = this.aceptaTerminos,
        comentario = this.comentario,
        profileImageUri = this.profileImageUri // <- AÑADIDO
    )
}

fun UserEntity.toModel(): FormModel {
    return FormModel(
        nombre = this.nombre,
        email = this.email,
        edad = this.edad,
        aceptaTerminos = this.aceptaTerminos,
        comentario = this.comentario,
        profileImageUri = this.profileImageUri // <- AÑADIDO
    )
}