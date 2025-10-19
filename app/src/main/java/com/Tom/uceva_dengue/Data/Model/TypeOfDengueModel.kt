package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class TypeOfDengueModel(
    @SerializedName("ID_TIPODENGUE")
    val ID_TIPODENGUE: Int,

    @SerializedName("NOMBRE_TIPODENGUE")
    val NOMBRE_TIPODENGUE: String,

    @SerializedName("ESTADO_TIPODENGUE")
    val ESTADO_TIPODENGUE: Boolean = true
)
