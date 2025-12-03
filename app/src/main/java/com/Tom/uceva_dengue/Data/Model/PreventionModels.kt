package com.Tom.uceva_dengue.Data.Model

data class PreventionCategory(
    val ID_CATEGORIA_PREVENCION: Int,
    val NOMBRE_CATEGORIA: String,
    val DESCRIPCION_CATEGORIA: String?,
    val ICONO: String?,
    val COLOR: String?,
    val ORDEN_VISUALIZACION: Int,
    val ESTADO_CATEGORIA: Boolean,
    val FECHA_CREACION: String?,
    val IMAGENES: List<PreventionImage>,
    val ITEMS: List<PreventionItem>
)

data class PreventionImage(
    val ID_IMAGEN_CATEGORIA: Int,
    val ID_IMAGEN_MONGO: String,
    val TITULO_IMAGEN: String?,
    val ORDEN_VISUALIZACION: Int
)

data class PreventionItem(
    val ID_ITEM_PREVENCION: Int,
    val FK_ID_CATEGORIA_PREVENCION: Int,
    val TITULO_ITEM: String,
    val DESCRIPCION_ITEM: String,
    val EMOJI_ITEM: String?,
    val ES_ADVERTENCIA: Boolean,
    val ORDEN_VISUALIZACION: Int,
    val ESTADO_ITEM: Boolean
)
