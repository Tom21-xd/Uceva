package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
import retrofit2.http.GET

interface BloodTypeService {
    @GET("BloodType/getBloodType")
    suspend fun getBloodTypes(): List<BloodTypeModel>
}