package com.Tom.uceva_dengue.Data.Model

data class UpdateUserRequest(
    val nombre: String,
    val correo: String,
    val direccion: String,
    val id_rol: Int,
    val id_genero: Int,
    val id_municipio: Int? = null
)
