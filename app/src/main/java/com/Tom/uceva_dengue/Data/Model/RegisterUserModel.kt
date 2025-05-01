package com.Tom.uceva_dengue.Data.Model

class RegisterUserModel(
    val NOMBRE_USUARIO: String?,
    val CORREO_USUARIO: String?,
    val CONTRASENIA_USUARIO: String?,
    val DIRECCION_USUARIO: String?,
    val FK_ID_ROL: Int,
    val FK_ID_MUNICIPIO: Int?,
    val FK_ID_TIPOSANGRE: Int,
    val FK_ID_GENERO: Int

)