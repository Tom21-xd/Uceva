package com.Tom.uceva_dengue.Data.Model

data class HospitalModel(
    val ID_HOSPITAL: Int,
    val NOMBRE_HOSPITAL: String,
    val ESTADO_HOSPITAL: Int,
    val DIRECCION_HOSPITAL: String,
    val LATITUD_HOSPITAL: String,
    val LONGITUD_HOSPITAL: String,
    val FK_ID_MUNICIPIO: Int,
    val IMAGEN_HOSPITAL: String,
    val CANTIDADCASOS_HOSPITAL: Int,
    val ID_DEPARTAMENTO: Int,
    val FK_ID_DEPARTAMENTO: Int,
    val NOMBRE_DEPARTAMENTO: String
)
