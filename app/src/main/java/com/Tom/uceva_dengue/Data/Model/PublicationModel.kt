package com.Tom.uceva_dengue.Data.Model

data class PublicationModel (
    val ID_PUBLICACION: Int,
    val TITULO_PUBLICACION: String,
    val IMAGEN_PUBLICACION: String,
    val DESCRIPCION_PUBLICACION: String,
    val FECHA_PUBLICACION: String,
    val FK_ID_USUARIO: Int,
    val NOMBRE_USUARIO:String,
)