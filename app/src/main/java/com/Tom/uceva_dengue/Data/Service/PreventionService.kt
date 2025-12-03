package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.PreventionCategory
import retrofit2.Response
import retrofit2.http.GET

interface PreventionService {
    @GET("Prevention/categories")
    suspend fun getPreventionCategories(): Response<List<PreventionCategory>>
}
