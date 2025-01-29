package com.Tom.uceva_dengue.Domain.Entities

import java.util.Date

data class Usuario(
    var Id: String = "",
    var Nombre: String = "",
    var Ciudad: String = "",
    var Departamento: String = "",
    var Correo: String = "",
    var Direccion: String = "",
    var FechaNacimiento: String? = null,
    var Genero: String = "",
    var personalMedico: Boolean = false,
    var profesion: String = "",
    var especialidadMedica: String = "",
    var registroMedico: String = ""
)
