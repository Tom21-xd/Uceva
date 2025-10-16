package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class NotificationModel(
    @SerializedName("ID_NOTIFICACION")
    val ID_NOTIFICACION: Int = 0,

    @SerializedName("CONTENIDO_NOTIFICACION")
    val CONTENIDO_NOTIFICACION: String = "",

    @SerializedName("FECHA_NOTIFICACION")
    val FECHA_NOTIFICACION: String = "",

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int = 0,

    @SerializedName("LEIDA_NOTIFICACION")
    val LEIDA_NOTIFICACION: Boolean = false,

    @SerializedName("ESTADO_NOTIFICACION")
    val ESTADO_NOTIFICACION: Boolean = true,

    // Nested object from backend
    @SerializedName("USUARIO")
    val USUARIO: UserInfo? = null,

    // Legacy fields for backward compatibility (deprecated)
    @Deprecated("Use CONTENIDO_NOTIFICACION instead", ReplaceWith("CONTENIDO_NOTIFICACION"))
    val NOMBRE_TIPONOTIFICACION: String? = null,

    @Deprecated("Use CONTENIDO_NOTIFICACION instead", ReplaceWith("CONTENIDO_NOTIFICACION"))
    val DESCRIPCION_TIPONOTIFICACION: String? = null
) {
    // Backward compatibility - computed properties
    val NOMBRE_USUARIO: String
        get() = USUARIO?.NOMBRE_USUARIO ?: ""

    val CORREO_USUARIO: String
        get() = USUARIO?.CORREO_USUARIO ?: ""

    val NOMBRE_ROL: String
        get() = USUARIO?.NOMBRE_ROL ?: ""
}

// Using shared UserInfo from SharedModels.kt
