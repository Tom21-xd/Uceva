package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class SymptomModel(
    @SerializedName("ID_SINTOMA")
    val ID_SINTOMA: Int,

    @SerializedName("NOMBRE_SINTOMA")
    val NOMBRE_SINTOMA: String?,

    @SerializedName("ESTADO_SINTOMA")
    val ESTADO_SINTOMA: Boolean = true
)
