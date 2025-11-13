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

// Response model for createHospital endpoint
data class CreateHospitalResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("hospital")
    val hospital: HospitalCreatedData?,

    @SerializedName("imagenId")
    val imagenId: String?
)

// Hospital data returned from backend on creation
data class HospitalCreatedData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: String,

    @SerializedName("longitude")
    val longitude: String,

    @SerializedName("cityId")
    val cityId: Int,

    @SerializedName("imageId")
    val imageId: String?,

    @SerializedName("isActive")
    val isActive: Boolean
)

// DTO for updating hospital
data class UpdateHospitalDto(
    @SerializedName("nombre")
    val nombre: String? = null,

    @SerializedName("direccion")
    val direccion: String? = null,

    @SerializedName("latitud")
    val latitud: String? = null,

    @SerializedName("longitud")
    val longitud: String? = null,

    @SerializedName("id_municipio")
    val idMunicipio: Int? = null,

    @SerializedName("imagen_id")
    val imagenId: String? = null
)

// Response model for updateHospital endpoint
data class UpdateHospitalResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("hospital")
    val hospital: HospitalModel?
)

// Response model for deleteHospital endpoint
data class DeleteHospitalResponse(
    @SerializedName("message")
    val message: String
)
