package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class HospitalModel(
    @SerializedName("ID_HOSPITAL")
    val ID_HOSPITAL: Int = 0,

    @SerializedName("NOMBRE_HOSPITAL")
    val NOMBRE_HOSPITAL: String = "",

    @SerializedName("ESTADO_HOSPITAL")
    val ESTADO_HOSPITAL: Boolean = true,

    @SerializedName("DIRECCION_HOSPITAL")
    val DIRECCION_HOSPITAL: String? = null,

    @SerializedName("LATITUD_HOSPITAL")
    val LATITUD_HOSPITAL: String? = null,

    @SerializedName("LONGITUD_HOSPITAL")
    val LONGITUD_HOSPITAL: String? = null,

    @SerializedName("FK_ID_MUNICIPIO")
    val FK_ID_MUNICIPIO: Int = 0,

    @SerializedName("IMAGEN_HOSPITAL")
    val IMAGEN_HOSPITAL: String? = null,

    // Nested object from backend
    @SerializedName("MUNICIPIO")
    val MUNICIPIO: CityInfo? = null,

    // Legacy field for backward compatibility (not from API)
    val CANTIDADCASOS_HOSPITAL: Int = 0
) {
    // Backward compatibility - computed properties that prioritize nested objects
    val NOMBRE_MUNICIPIO: String
        get() = MUNICIPIO?.NOMBRE_MUNICIPIO ?: ""

    val NOMBRE_DEPARTAMENTO: String
        get() = MUNICIPIO?.DEPARTAMENTO?.NOMBRE_DEPARTAMENTO ?: ""

    val ID_DEPARTAMENTO: Int
        get() = MUNICIPIO?.FK_ID_DEPARTAMENTO ?: 0

    val FK_ID_DEPARTAMENTO: Int
        get() = MUNICIPIO?.FK_ID_DEPARTAMENTO ?: 0
}

// Using shared CityInfo and DepartmentInfo from SharedModels.kt
