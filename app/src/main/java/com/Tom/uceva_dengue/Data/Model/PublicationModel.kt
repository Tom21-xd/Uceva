package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class PublicationModel (
    @SerializedName("ID_PUBLICACION")
    val ID_PUBLICACION: Int,

    @SerializedName("TITULO_PUBLICACION")
    val TITULO_PUBLICACION: String,

    @SerializedName("IMAGEN_PUBLICACION")
    val IMAGEN_PUBLICACION: String?,

    @SerializedName("DESCRIPCION_PUBLICACION")
    val DESCRIPCION_PUBLICACION: String,

    @SerializedName("FECHA_PUBLICACION")
    val FECHA_PUBLICACION: String,

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int,

    @SerializedName("ESTADO_PUBLICACION")
    val ESTADO_PUBLICACION: Boolean = true,

    // ===== NUEVOS CAMPOS - CATEGORÍA Y PRIORIDAD =====

    @SerializedName("FK_ID_CATEGORIA")
    val FK_ID_CATEGORIA: Int? = null,

    @SerializedName("NIVEL_PRIORIDAD")
    val NIVEL_PRIORIDAD: String? = "Normal", // Baja, Normal, Alta, Urgente

    @SerializedName("FIJADA")
    val FIJADA: Boolean = false,

    @SerializedName("FECHA_EXPIRACION")
    val FECHA_EXPIRACION: String? = null,

    // ===== NUEVOS CAMPOS - GEOLOCALIZACIÓN =====

    @SerializedName("LATITUD")
    val LATITUD: Double? = null,

    @SerializedName("LONGITUD")
    val LONGITUD: Double? = null,

    @SerializedName("FK_ID_CIUDAD")
    val FK_ID_CIUDAD: Int? = null,

    @SerializedName("FK_ID_DEPARTAMENTO")
    val FK_ID_DEPARTAMENTO: Int? = null,

    @SerializedName("DIRECCION")
    val DIRECCION: String? = null,

    // ===== NUEVOS CAMPOS - NOTIFICACIONES =====

    @SerializedName("ENVIAR_NOTIFICACION_PUSH")
    val ENVIAR_NOTIFICACION_PUSH: Boolean = false,

    @SerializedName("NOTIFICACION_ENVIADA")
    val NOTIFICACION_ENVIADA: Boolean = false,

    @SerializedName("FECHA_ENVIO_NOTIFICACION")
    val FECHA_ENVIO_NOTIFICACION: String? = null,

    // ===== NUEVOS CAMPOS - PUBLICACIÓN PROGRAMADA =====

    @SerializedName("FECHA_PUBLICACION_PROGRAMADA")
    val FECHA_PUBLICACION_PROGRAMADA: String? = null,

    @SerializedName("PUBLICADA")
    val PUBLICADA: Boolean = true,

    // ===== NESTED OBJECTS =====

    @SerializedName("USUARIO")
    val USUARIO: UserInfo? = null,

    @SerializedName("CATEGORIA")
    val CATEGORIA: PublicationCategoryModel? = null,

    @SerializedName("CIUDAD")
    val CIUDAD: CityInfo? = null,

    @SerializedName("DEPARTAMENTO")
    val DEPARTAMENTO: DepartmentInfo? = null,

    // ===== COLECCIONES - INTERACCIONES =====

    @SerializedName("ETIQUETAS")
    val ETIQUETAS: List<PublicationTagModel>? = null,

    @SerializedName("REACCIONES")
    val REACCIONES: List<PublicationReactionModel>? = null,

    @SerializedName("COMENTARIOS")
    val COMENTARIOS: List<PublicationCommentModel>? = null,

    // ===== ESTADÍSTICAS CALCULADAS =====

    @SerializedName("TOTAL_REACCIONES")
    val TOTAL_REACCIONES: Int? = 0,

    @SerializedName("TOTAL_COMENTARIOS")
    val TOTAL_COMENTARIOS: Int? = 0,

    @SerializedName("TOTAL_VISTAS")
    val TOTAL_VISTAS: Int? = 0,

    @SerializedName("TOTAL_GUARDADOS")
    val TOTAL_GUARDADOS: Int? = 0,

    @SerializedName("USUARIO_HA_REACCIONADO")
    val USUARIO_HA_REACCIONADO: Boolean? = false,

    @SerializedName("USUARIO_HA_GUARDADO")
    val USUARIO_HA_GUARDADO: Boolean? = false,

    // Deprecated: Keep for backward compatibility
    val NOMBRE_USUARIO: String? = USUARIO?.NOMBRE_USUARIO
)

/**
 * Request para crear/actualizar publicación
 */
data class CreatePublicationRequest(
    @SerializedName("TITULO_PUBLICACION")
    val TITULO_PUBLICACION: String,

    @SerializedName("DESCRIPCION_PUBLICACION")
    val DESCRIPCION_PUBLICACION: String,

    @SerializedName("IMAGEN_PUBLICACION")
    val IMAGEN_PUBLICACION: String? = null,

    @SerializedName("FK_ID_CATEGORIA")
    val FK_ID_CATEGORIA: Int? = null,

    @SerializedName("NIVEL_PRIORIDAD")
    val NIVEL_PRIORIDAD: String = "Normal",

    @SerializedName("FIJADA")
    val FIJADA: Boolean = false,

    @SerializedName("ENVIAR_NOTIFICACION_PUSH")
    val ENVIAR_NOTIFICACION_PUSH: Boolean = false,

    @SerializedName("LATITUD")
    val LATITUD: Double? = null,

    @SerializedName("LONGITUD")
    val LONGITUD: Double? = null,

    @SerializedName("FK_ID_CIUDAD")
    val FK_ID_CIUDAD: Int? = null,

    @SerializedName("DIRECCION")
    val DIRECCION: String? = null,

    @SerializedName("FECHA_PUBLICACION_PROGRAMADA")
    val FECHA_PUBLICACION_PROGRAMADA: String? = null,

    @SerializedName("PUBLICADA")
    val PUBLICADA: Boolean = true,

    // IDs de etiquetas a asociar
    @SerializedName("ETIQUETAS_IDS")
    val ETIQUETAS_IDS: List<Int>? = null
)