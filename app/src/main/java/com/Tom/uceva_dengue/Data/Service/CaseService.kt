package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseModel
import com.Tom.uceva_dengue.Data.Model.CaseStateModel
import com.Tom.uceva_dengue.Data.Model.CreateCaseModel
import com.Tom.uceva_dengue.Data.Model.UpdateCaseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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
}