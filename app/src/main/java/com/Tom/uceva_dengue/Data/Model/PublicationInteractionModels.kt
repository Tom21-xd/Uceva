package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Reacción de usuario a una publicación
 * Tipos: MeGusta, Importante, Util
 */
data class PublicationReactionModel(
    @SerializedName("ID_REACCION")
    val ID_REACCION: Int,

    @SerializedName("FK_ID_PUBLICACION")
    val FK_ID_PUBLICACION: Int,

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int,

    @SerializedName("TIPO_REACCION")
    val TIPO_REACCION: String, // MeGusta, Importante, Util

    @SerializedName("FECHA_REACCION")
    val FECHA_REACCION: String,

    @SerializedName("USUARIO")
    val USUARIO: UserInfo? = null
)

/**
 * Request para crear/actualizar reacción
 */
data class CreateReactionRequest(
    @SerializedName("TIPO_REACCION")
    val TIPO_REACCION: String = "MeGusta"
)

/**
 * Comentario en una publicación
 * Soporta hilos de comentarios (respuestas)
 */
data class PublicationCommentModel(
    @SerializedName("ID_COMENTARIO")
    val ID_COMENTARIO: Int,

    @SerializedName("FK_ID_PUBLICACION")
    val FK_ID_PUBLICACION: Int,

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int,

    @SerializedName("CONTENIDO_COMENTARIO")
    val CONTENIDO_COMENTARIO: String,

    @SerializedName("FK_ID_COMENTARIO_PADRE")
    val FK_ID_COMENTARIO_PADRE: Int? = null, // Para hilos de respuestas

    @SerializedName("FECHA_COMENTARIO")
    val FECHA_COMENTARIO: String,

    @SerializedName("ESTADO_COMENTARIO")
    val ESTADO_COMENTARIO: Boolean = true,

    @SerializedName("USUARIO")
    val USUARIO: UserInfo? = null,

    // Lista de respuestas a este comentario
    @SerializedName("RESPUESTAS")
    val RESPUESTAS: List<PublicationCommentModel>? = null
)

/**
 * Request para crear comentario
 * Debe coincidir con CreateCommentDto del backend
 */
data class CreateCommentRequest(
    @SerializedName("Content")
    val Content: String,

    @SerializedName("ParentCommentId")
    val ParentCommentId: Int? = null
)

/**
 * Publicación guardada/favorita
 */
data class SavedPublicationModel(
    @SerializedName("ID_GUARDADO")
    val ID_GUARDADO: Int,

    @SerializedName("FK_ID_PUBLICACION")
    val FK_ID_PUBLICACION: Int,

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int,

    @SerializedName("FECHA_GUARDADO")
    val FECHA_GUARDADO: String,

    // Nested publication si el backend lo retorna
    @SerializedName("PUBLICACION")
    val PUBLICACION: PublicationModel? = null
)

/**
 * Vista/lectura de publicación (para estadísticas)
 */
data class PublicationViewModel(
    @SerializedName("ID_LECTURA")
    val ID_LECTURA: Int,

    @SerializedName("FK_ID_PUBLICACION")
    val FK_ID_PUBLICACION: Int,

    @SerializedName("FK_ID_USUARIO")
    val FK_ID_USUARIO: Int,

    @SerializedName("FECHA_LECTURA")
    val FECHA_LECTURA: String,

    @SerializedName("TIEMPO_LECTURA_SEGUNDOS")
    val TIEMPO_LECTURA_SEGUNDOS: Int? = null
)

/**
 * Request para registrar vista
 */
data class RegisterViewRequest(
    @SerializedName("TIEMPO_LECTURA_SEGUNDOS")
    val TIEMPO_LECTURA_SEGUNDOS: Int? = null
)

/**
 * Estadísticas de una publicación
 */
data class PublicationStatsModel(
    @SerializedName("TOTAL_REACCIONES")
    val TOTAL_REACCIONES: Int = 0,

    @SerializedName("TOTAL_COMENTARIOS")
    val TOTAL_COMENTARIOS: Int = 0,

    @SerializedName("TOTAL_VISTAS")
    val TOTAL_VISTAS: Int = 0,

    @SerializedName("TOTAL_GUARDADOS")
    val TOTAL_GUARDADOS: Int = 0,

    @SerializedName("PROMEDIO_LECTURA_SEGUNDOS")
    val PROMEDIO_LECTURA_SEGUNDOS: Double? = null,

    // Desglose por tipo de reacción
    @SerializedName("REACCIONES_POR_TIPO")
    val REACCIONES_POR_TIPO: Map<String, Int>? = null
)
