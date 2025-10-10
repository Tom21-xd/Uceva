package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import com.Tom.uceva_dengue.Data.Model.CreateCaseModel
import com.Tom.uceva_dengue.Data.Model.UpdateCaseModel
import retrofit2.Response
import retrofit2.http.*

interface CaseService {
    @GET("Case/getCases")
    suspend fun getCases(): Response<List<CaseModel>>

    @POST("Case/creatreCase")
    suspend fun createCase(@Body case: CreateCaseModel): Response<Void>

    @GET("Case/getCaseById")
    suspend fun getCaseById(@Query("id") id: String): Response<CaseModel>

    @GET("Case/getStateCase")
    suspend fun getStateCase(): Response<List<CaseStateModel>>

    @PATCH("Case/updateCase/{id}")
    suspend fun updateCase(
        @Path("id") id: Int,
        @Body update: UpdateCaseModel
    ): Response<Void>

    // HU-006: Eliminar caso
    @DELETE("Case/deleteCase/{id}")
    suspend fun deleteCase(@Path("id") id: Int): Response<Map<String, String>>

    // HU-012: Historial de casos de un paciente
    @GET("Case/getCaseHistory/{userId}")
    suspend fun getCaseHistory(@Path("userId") userId: Int): Response<List<CaseModel>>

    // Obtener casos por hospital
    @GET("Case/getCasesByHospital/{hospitalId}")
    suspend fun getCasesByHospital(@Path("hospitalId") hospitalId: Int): Response<List<CaseModel>>
}