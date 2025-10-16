package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Shared models used across different API responses for nested objects
 */

data class UserInfo(
    val ID_USUARIO: Int,
    val NOMBRE_USUARIO: String,
    val CORREO_USUARIO: String,
    val NOMBRE_ROL: String? = null
)

data class CityInfo(
    @SerializedName("ID_MUNICIPIO")
    val ID_MUNICIPIO: Int,

    @SerializedName("NOMBRE_MUNICIPIO")
    val NOMBRE_MUNICIPIO: String,

    @SerializedName("FK_ID_DEPARTAMENTO")
    val FK_ID_DEPARTAMENTO: Int,

    @SerializedName("DEPARTAMENTO")
    val DEPARTAMENTO: DepartmentInfo? = null
)

data class DepartmentInfo(
    @SerializedName("ID_DEPARTAMENTO")
    val ID_DEPARTAMENTO: Int,

    @SerializedName("NOMBRE_DEPARTAMENTO")
    val NOMBRE_DEPARTAMENTO: String
)
