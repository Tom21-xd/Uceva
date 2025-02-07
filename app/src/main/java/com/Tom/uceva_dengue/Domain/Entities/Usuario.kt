package com.Tom.uceva_dengue.Domain.Entities

data class Usuario(
    var ID_USUARIO: Int = 0,
    var NOMBRE_USUARIO: String? = null,
    var CORREO_USUARIO: String? = null,
    var CONTRASENIA_USUARIO: String? = null,
    var DIRECCION_USUARIO: String? = null,
    var FK_ID_ROL: Int = 0,
    var NOMBRE_ROL: String? = null,
    var FK_ID_MUNICIPIO: Int = 0,
    var NOMBRE_MUNICIPIO: String? = null,
    var FK_ID_TIPOSANGRE: Int = 0,
    var NOMBRE_TIPOSANGRE: String? = null,
    var FK_ID_GENERO: Int = 0,
    var NOMBRE_GENERO: String? = null,
    var ID_DEPARTAMENTO: Int = 0,
    var NOMBRE_ESTADOUSUARIO: String? = null,
    var FK_ID_ESTADOUSUARIO: Int = 0
)
