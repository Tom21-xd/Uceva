package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Etiqueta/Tag para publicaciones
 * Permite etiquetar publicaciones con hashtags como #eliminacriaderos, #fumigación, etc.
 */
data class PublicationTagModel(
    @SerializedName("ID_ETIQUETA")
    val ID_ETIQUETA: Int,

    @SerializedName("NOMBRE_ETIQUETA")
    val NOMBRE_ETIQUETA: String, // #eliminacriaderos, #prevención, etc.

    @SerializedName("ESTADO_ETIQUETA")
    val ESTADO_ETIQUETA: Boolean = true
)

/**
 * Relación entre publicación y etiqueta
 */
data class PublicationTagRelationModel(
    @SerializedName("ID_PUBLICACION_ETIQUETA")
    val ID_PUBLICACION_ETIQUETA: Int,

    @SerializedName("FK_ID_PUBLICACION")
    val FK_ID_PUBLICACION: Int,

    @SerializedName("FK_ID_ETIQUETA")
    val FK_ID_ETIQUETA: Int,

    // Nested objects si el backend los retorna
    @SerializedName("TAG")
    val TAG: PublicationTagModel? = null
)
