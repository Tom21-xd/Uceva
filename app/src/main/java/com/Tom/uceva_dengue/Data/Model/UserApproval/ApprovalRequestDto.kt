package com.Tom.uceva_dengue.Data.Model.UserApproval

import com.google.gson.annotations.SerializedName

/**
 * DTO para solicitud de aprobación de usuario
 * Corresponde al modelo del backend ApprovalRequestDto
 */
data class ApprovalRequestDto(
    @SerializedName("Id")
    val id: Int = 0,

    @SerializedName("UserId")
    val userId: Int = 0,

    @SerializedName("UserName")
    val userName: String? = null,

    @SerializedName("UserEmail")
    val userEmail: String? = null,

    @SerializedName("Status")
    val status: String? = null, // PENDIENTE, APROBADO, RECHAZADO

    @SerializedName("RequestedRoleId")
    val requestedRoleId: Int = 0,

    @SerializedName("RequestedRoleName")
    val requestedRoleName: String? = null,

    @SerializedName("RejectionReason")
    val rejectionReason: String? = null,

    @SerializedName("ApprovedByAdminId")
    val approvedByAdminId: Int? = null,

    @SerializedName("ApprovedByAdminName")
    val approvedByAdminName: String? = null,

    @SerializedName("RequestDate")
    val requestDate: String? = null,

    @SerializedName("ResolutionDate")
    val resolutionDate: String? = null,

    @SerializedName("RethusData")
    val rethusData: String? = null,

    @SerializedName("RethusError")
    val rethusError: String? = null
)
