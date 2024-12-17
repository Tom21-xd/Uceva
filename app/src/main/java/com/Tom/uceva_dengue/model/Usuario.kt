package com.Tom.uceva_dengue.model

import java.util.Date

data class Usuario(
    var Id: String = "",
    var Nombre: String = "",
    var Ciudad: String = "",
    var Correo: String = "",
    var Direccion: String = "",
    var FechaNacimiento: Date? = null,
    var Genero: String = ""
)
