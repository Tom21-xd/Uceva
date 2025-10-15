package com.Tom.uceva_dengue.Data.Model

data class UserModel(
    val ID_USUARIO: Int = 0,
    val NOMBRE_USUARIO: String? = null,
    val CORREO_USUARIO: String? = null,
    val CONTRASENIA_USUARIO: String? = null,
    val DIRECCION_USUARIO: String? = null,
    val FK_ID_ROL: Int = 0,
    val NOMBRE_ROL: String? = null,
    val FK_ID_MUNICIPIO: Int? = null,
    val NOMBRE_MUNICIPIO: String? = null,
    val FK_ID_TIPOSANGRE: Int = 0,
    val NOMBRE_TIPOSANGRE: String? = null,
    val FK_ID_GENERO: Int = 0,
    val NOMBRE_GENERO: String? = null,
    val ID_DEPARTAMENTO: Int = 0,
    val NOMBRE_ESTADOUSUARIO: String? = null
)

