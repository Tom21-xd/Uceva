package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    @SerializedName("id_usuario")
    val userId: Int,

    @SerializedName("fcm_token")
    val fcmToken: String
)

data class FCMTokenResponse(
    @SerializedName("message")
    val message: String
)
