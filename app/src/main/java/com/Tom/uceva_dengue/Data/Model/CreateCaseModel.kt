package com.Tom.uceva_dengue.Data.Model

data class CreateCaseModel(
    val descripcion: String,
    val id_hospital: Int?,  // Ahora puede ser null (casos sin hospital)
    val id_tipoDengue: Int,
    val id_paciente: Int?,  // Ahora puede ser null (casos anónimos)
    val id_personalMedico: Int,
    val direccion: String,

    // Campos epidemiológicos nuevos
    val anio_reporte: Int? = null,
    val edad: Int? = null,
    val nombre_temporal: String? = null,
    val barrio: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null
)
