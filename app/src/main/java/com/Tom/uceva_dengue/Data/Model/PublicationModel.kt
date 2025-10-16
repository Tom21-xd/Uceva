package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class PublicationModel (
    val ID_PUBLICACION: Int,
    val TITULO_PUBLICACION: String,
    val IMAGEN_PUBLICACION: String?,
    val DESCRIPCION_PUBLICACION: String,
    val FECHA_PUBLICACION: String,
    val FK_ID_USUARIO: Int,

    // Nested user object from backend
    @SerializedName("USUARIO")
    val USUARIO: UserInfo? = null,

    // Deprecated: Keep for backward compatibility, will be removed
    val NOMBRE_USUARIO: String? = USUARIO?.NOMBRE_USUARIO
)

/**
 * Basic user information embedded in publication responses
 */
data class UserInfo(
    val ID_USUARIO: Int,
    val NOMBRE_USUARIO: String,
    val CORREO_USUARIO: String,
    val NOMBRE_ROL: String?
)