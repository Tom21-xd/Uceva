package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CityModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CityService {
    @GET("Department/getCities")
    suspend fun getCities(@Query("filter") filter: String): List<CityModel>
}
