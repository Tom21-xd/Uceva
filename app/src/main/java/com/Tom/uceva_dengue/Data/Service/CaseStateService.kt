package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import retrofit2.Response
import retrofit2.http.*

interface CaseStateService {
    @GET("CaseState/getCaseStates")
    suspend fun getCaseStates(): Response<List<CaseStateModel>>

    @GET("CaseState/{id}")
    suspend fun getCaseStateById(@Path("id") id: Int): Response<CaseStateModel>

    @POST("CaseState")
    suspend fun createCaseState(@Body caseState: CaseStateModel): Response<CaseStateModel>

    @PUT("CaseState/{id}")
    suspend fun updateCaseState(@Path("id") id: Int, @Body caseState: CaseStateModel): Response<Any>

    @DELETE("CaseState/{id}")
    suspend fun deleteCaseState(@Path("id") id: Int): Response<Any>
}
