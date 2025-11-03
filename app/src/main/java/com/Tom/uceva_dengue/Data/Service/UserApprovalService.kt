package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.UserApproval.ApprovalRequestDto
import com.Tom.uceva_dengue.Data.Model.UserApproval.ApproveUserRequest
import com.Tom.uceva_dengue.Data.Model.UserApproval.ApproveUserResponse
import com.Tom.uceva_dengue.Data.Model.UserApproval.RejectUserRequest
import com.Tom.uceva_dengue.Data.Model.UserApproval.RejectUserResponse
import com.Tom.uceva_dengue.Data.Model.UserApproval.UserApprovalStatusResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para gestión de solicitudes de aprobación de usuarios
 * Endpoints del controlador UserApprovalController
 */
interface UserApprovalService {

    /**
     * Obtiene todas las solicitudes de aprobación pendientes
     * Requiere permiso: USER_APPROVAL_VIEW
     */
    @GET("UserApproval/pending")
    suspend fun getPendingApprovals(): Response<List<ApprovalRequestDto>>

    /**
     * Obtiene todas las solicitudes de aprobación (historial completo)
     * Requiere permiso: USER_APPROVAL_HISTORY
     */
    @GET("UserApproval/all")
    suspend fun getAllApprovals(): Response<List<ApprovalRequestDto>>

    /**
     * Aprueba una solicitud de usuario y le cambia el rol
     * Requiere permiso: USER_APPROVAL_APPROVE
     */
    @POST("UserApproval/approve/{adminId}")
    @Headers("Content-Type: application/json")
    suspend fun approveUser(
        @Path("adminId") adminId: Int,
        @Body request: ApproveUserRequest
    ): Response<ApproveUserResponse>

    /**
     * Rechaza una solicitud de usuario
     * Requiere permiso: USER_APPROVAL_REJECT
     */
    @POST("UserApproval/reject/{adminId}")
    @Headers("Content-Type: application/json")
    suspend fun rejectUser(
        @Path("adminId") adminId: Int,
        @Body request: RejectUserRequest
    ): Response<RejectUserResponse>

    /**
     * Obtiene el estado de aprobación de un usuario específico
     */
    @GET("UserApproval/status/{userId}")
    suspend fun getUserApprovalStatus(
        @Path("userId") userId: Int
    ): Response<UserApprovalStatusResponse>
}
