package com.Tom.uceva_dengue.Data.Model.UserApproval

import com.google.gson.annotations.SerializedName

/**
 * Response del estado de aprobación de un usuario
 */
data class UserApprovalStatusResponse(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("tieneSolicitud")
    val tieneSolicitud: Boolean,

    @SerializedName("solicitud")
    val solicitud: ApprovalRequestDto? = null,

    @SerializedName("mensaje")
    val mensaje: String? = null
)
