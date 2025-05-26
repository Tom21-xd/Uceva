package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.HospitalModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HospitalService {

    @GET("Hospital/getHospitals")
    suspend fun getHospitals(): Response<List<HospitalModel>>

    @GET("Hospital/filterHospitals")
    suspend fun filterHospitals(@Query("name") name: String): Response<List<HospitalModel>>

    @GET("Hospital/getHospitaToCity")
    suspend fun getHospitalsByCity(@Query("filtro") cityId: Int): Response<List<HospitalModel>>
}
