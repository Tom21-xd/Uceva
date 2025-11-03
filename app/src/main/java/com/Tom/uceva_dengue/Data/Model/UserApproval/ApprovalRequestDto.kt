package com.Tom.uceva_dengue.Data.Model.UserApproval

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de aprobación de usuario
 * Corresponde al modelo del backend ApprovalRequestDto
 */
data class ApprovalRequestDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("userEmail")
    val userEmail: String,

    @SerializedName("status")
    val status: String, // PENDIENTE, APROBADO, RECHAZADO

    @SerializedName("requestedRoleId")
    val requestedRoleId: Int,

    @SerializedName("requestedRoleName")
    val requestedRoleName: String,

    @SerializedName("rejectionReason")
    val rejectionReason: String? = null,

    @SerializedName("approvedByAdminId")
    val approvedByAdminId: Int? = null,

    @SerializedName("approvedByAdminName")
    val approvedByAdminName: String? = null,

    @SerializedName("requestDate")
    val requestDate: String,

    @SerializedName("resolutionDate")
    val resolutionDate: String? = null,

    @SerializedName("rethusData")
    val rethusData: String? = null,

    @SerializedName("rethusError")
    val rethusError: String? = null
)
