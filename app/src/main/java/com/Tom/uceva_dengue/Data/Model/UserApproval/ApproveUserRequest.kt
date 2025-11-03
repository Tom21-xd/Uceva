package com.Tom.uceva_dengue.Data.Model.UserApproval

import com.google.gson.annotations.SerializedName

/**
 * Request para aprobar un usuario y cambiarle el rol
 */
data class ApproveUserRequest(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("newRoleId")
    val newRoleId: Int
)

/**
 * Response al aprobar un usuario
 */
data class ApproveUserResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("usuario")
    val usuario: ApprovedUserData
)

data class ApprovedUserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("nuevoRol")
    val nuevoRol: String
)
