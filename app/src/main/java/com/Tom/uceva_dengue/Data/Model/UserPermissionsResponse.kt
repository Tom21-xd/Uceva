package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Response model for GET /api/Permission/current-user endpoint
 */
data class UserPermissionsResponse(
    @SerializedName("userId")
    val userId: Int,

    @SerializedName("roleId")
    val roleId: Int,

    @SerializedName("roleName")
    val roleName: String,

    @SerializedName("permissions")
    val permissions: List<String>,

    @SerializedName("totalPermissions")
    val totalPermissions: Int
)
