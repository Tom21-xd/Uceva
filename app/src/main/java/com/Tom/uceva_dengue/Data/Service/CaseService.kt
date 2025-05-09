package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
import com.Tom.uceva_dengue.Data.Model.CaseModel
import retrofit2.http.GET

interface CaseService {
    @GET("Case/getCases")
    suspend fun getCase(): List<CaseModel>
}