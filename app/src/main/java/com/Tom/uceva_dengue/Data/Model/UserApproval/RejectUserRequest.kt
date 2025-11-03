package com.Tom.uceva_dengue.Data.Model.UserApproval

import com.google.gson.annotations.SerializedName

/**
 * Request para rechazar un usuario
 */
data class RejectUserRequest(
    @SerializedName("UserId")
    val userId: Int,

    @SerializedName("RejectionReason")
    val rejectionReason: String
)

/**
 * Response al rechazar un usuario
 */
data class RejectUserResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("usuario")
    val usuario: RejectedUserData,

    @SerializedName("motivo")
    val motivo: String
)

data class RejectedUserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String
)
