package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.DiagnosticRequest
import com.Tom.uceva_dengue.Data.Model.DiagnosticResponse
import com.Tom.uceva_dengue.Data.Model.SymptomModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DiagnosticService {
    // HU-013: Inferir tipo de dengue por síntomas
    @POST("Diagnostic/diagnoseDengue")
    suspend fun diagnoseDengue(@Body request: DiagnosticRequest): Response<DiagnosticResponse>

    // Obtener lista de síntomas disponibles
    @GET("Diagnostic/getSymptoms")
    suspend fun getSymptoms(): Response<List<SymptomModel>>
}
