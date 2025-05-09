package com.Tom.uceva_dengue.Data.Model


data class CaseModel(
    val ID_CASOREPORTADO: Int,
    val DESCRIPCION_CASOREPORTADO: String,
    val FECHA_CASOREPORTADO: String,
    val FK_ID_ESTADOCASO: Int,
    val NOMBRE_ESTADOCASO: String,
    val ID_DEPARTAMENTO: Int,
    val ID_MUNICIPIO: Int,
    val FK_ID_HOSPITAL: Int,
    val FK_ID_TIPODENGUE: Int,
    val FK_ID_PACIENTE: Int,
    val NOMBRE_PACIENTE: String,
    val FK_ID_PERSONALMEDICO: Int,
    val NOMBRE_PERSONALMEDICO: String,
    val FECHAFINALIZACION_CASO: String,
    val DIRECCION_CASOREPORTADO: String
)
