package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Categoría de publicación
 * Permite organizar publicaciones por tipo: Alerta, Prevención, Educación, etc.
 */
data class PublicationCategoryModel(
    @SerializedName("ID_CATEGORIA_PUBLICACION")
    val ID_CATEGORIA_PUBLICACION: Int,

    @SerializedName("NOMBRE_CATEGORIA")
    val NOMBRE_CATEGORIA: String,

    @SerializedName("DESCRIPCION_CATEGORIA")
    val DESCRIPCION_CATEGORIA: String?,

    @SerializedName("ICONO")
    val ICONO: String?, // alert, shield, book, news, event, science

    @SerializedName("COLOR")
    val COLOR: String?, // red, green, blue, gray, purple, teal

    @SerializedName("ESTADO_CATEGORIA")
    val ESTADO_CATEGORIA: Boolean = true
)
