package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class CaseModel(
    @SerializedName("ID_CASO")
    val ID_CASOREPORTADO: Int = 0,

    @SerializedName("DESCRIPCION_CASOREPORTADO")
    val DESCRIPCION_CASOREPORTADO: String = "",

    @SerializedName("FECHA_CASOREPORTADO")
    val FECHA_CASOREPORTADO: String = "",

    @SerializedName("FK_ID_ESTADOCASO")
    val FK_ID_ESTADOCASO: Int = 0,

    @SerializedName("FK_ID_HOSPITAL")
    val FK_ID_HOSPITAL: Int = 0,

    @SerializedName("FK_ID_TIPODENGUE")
    val FK_ID_TIPODENGUE: Int = 0,

    @SerializedName("FK_ID_PACIENTE")
    val FK_ID_PACIENTE: Int = 0,

    @SerializedName("FK_ID_PERSONALMEDICO")
    val FK_ID_PERSONALMEDICO: Int? = null,

    @SerializedName("FECHAFINALIZACION_CASO")
    val FECHAFINALIZACION_CASO: String? = null,

    @SerializedName("DIRECCION_CASOREPORTADO")
    val DIRECCION_CASOREPORTADO: String? = null,

    @SerializedName("ESTADO_CASO")
    val ESTADO_CASO: Boolean = true,

    // Nested objects from backend
    @SerializedName("ESTADO")
    val ESTADO: CaseStateInfo? = null,

    @SerializedName("HOSPITAL")
    val HOSPITAL: HospitalInfo? = null,

    @SerializedName("TIPO_DENGUE")
    val TIPO_DENGUE: TypeOfDengueInfo? = null,

    @SerializedName("PACIENTE")
    val PACIENTE: UserInfo? = null,

    @SerializedName("PERSONAL_MEDICO")
    val PERSONAL_MEDICO: UserInfo? = null
) {
    // Backward compatibility - computed properties that prioritize nested objects
    val NOMBRE_ESTADOCASO: String
        get() = ESTADO?.NOMBRE_ESTADOCASO ?: ""

    val NOMBRE_HOSPITAL: String
        get() = HOSPITAL?.NOMBRE_HOSPITAL ?: ""

    val NOMBRE_TIPODENGUE: String
        get() = TIPO_DENGUE?.NOMBRE_TIPODENGUE ?: ""

    val NOMBRE_PACIENTE: String
        get() = PACIENTE?.NOMBRE_USUARIO ?: ""

    val NOMBRE_PERSONALMEDICO: String
        get() = PERSONAL_MEDICO?.NOMBRE_USUARIO ?: ""

    val NOMBRE_MUNICIPIO: String
        get() = HOSPITAL?.MUNICIPIO?.NOMBRE_MUNICIPIO ?: ""

    val NOMBRE_DEPARTAMENTO: String
        get() = HOSPITAL?.MUNICIPIO?.DEPARTAMENTO?.NOMBRE_DEPARTAMENTO ?: ""

    val ID_MUNICIPIO: Int
        get() = HOSPITAL?.FK_ID_MUNICIPIO ?: 0

    val ID_DEPARTAMENTO: Int
        get() = HOSPITAL?.MUNICIPIO?.FK_ID_DEPARTAMENTO ?: 0
}

data class CaseStateInfo(
    @SerializedName("ID_ESTADOCASO")
    val ID_ESTADOCASO: Int,

    @SerializedName("NOMBRE_ESTADOCASO")
    val NOMBRE_ESTADOCASO: String
)

data class HospitalInfo(
    @SerializedName("ID_HOSPITAL")
    val ID_HOSPITAL: Int,

    @SerializedName("NOMBRE_HOSPITAL")
    val NOMBRE_HOSPITAL: String,

    @SerializedName("DIRECCION_HOSPITAL")
    val DIRECCION_HOSPITAL: String? = null,

    @SerializedName("LATITUD_HOSPITAL")
    val LATITUD_HOSPITAL: String? = null,

    @SerializedName("LONGITUD_HOSPITAL")
    val LONGITUD_HOSPITAL: String? = null,

    @SerializedName("FK_ID_MUNICIPIO")
    val FK_ID_MUNICIPIO: Int,

    @SerializedName("MUNICIPIO")
    val MUNICIPIO: CityInfo? = null
)

data class TypeOfDengueInfo(
    @SerializedName("ID_TIPODENGUE")
    val ID_TIPODENGUE: Int,

    @SerializedName("NOMBRE_TIPODENGUE")
    val NOMBRE_TIPODENGUE: String,

    @SerializedName("ESTADO_TIPODENGUE")
    val ESTADO_TIPODENGUE: Boolean = true
)
