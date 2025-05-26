package com.Tom.uceva_dengue.Data.Model

data class CreateCaseModel(
    val descripcion: String,
    val id_hospital: Int,
    val id_tipoDengue: Int,
    val id_paciente: Int,
    val id_personalMedico: Int,
    val direccion: String
)
