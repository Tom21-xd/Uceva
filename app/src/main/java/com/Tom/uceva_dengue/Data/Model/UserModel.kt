package com.Tom.uceva_dengue.Data.Model

data class UserModel(
    val ID_USUARIO: Int,
    val NOMBRE_USUARIO: String?,
    val CORREO_USUARIO: String?,
    val CONTRASENIA_USUARIO: String?,
    val DIRECCION_USUARIO: String?,
    val FK_ID_ROL: Int,
    val NOMBRE_ROL: String?,
    val FK_ID_MUNICIPIO: Int?,
    val NOMBRE_MUNICIPIO: String?,
    val FK_ID_TIPOSANGRE: Int,
    val NOMBRE_TIPOSANGRE: String?,
    val FK_ID_GENERO: Int,
    val NOMBRE_GENERO: String?,
    val ID_DEPARTAMENTO: Int,
    val NOMBRE_ESTADOUSUARIO: String?
)

