package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.SymptomModel
import retrofit2.Response
import retrofit2.http.*

interface SymptomService {
    @GET("Symptom/getSymptoms")
    suspend fun getSymptoms(): Response<List<SymptomModel>>

    @GET("Symptom/{id}")
    suspend fun getSymptomById(@Path("id") id: Int): Response<SymptomModel>

    @POST("Symptom")
    suspend fun createSymptom(@Body symptom: SymptomModel): Response<SymptomModel>

    @PUT("Symptom/{id}")
    suspend fun updateSymptom(@Path("id") id: Int, @Body symptom: SymptomModel): Response<Any>

    @DELETE("Symptom/{id}")
    suspend fun deleteSymptom(@Path("id") id: Int): Response<Any>
}
