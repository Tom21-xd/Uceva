package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseEvolutionModel
import com.Tom.uceva_dengue.Data.Model.CaseEvolutionSummaryModel
import com.Tom.uceva_dengue.Data.Model.CreateCaseEvolutionRequest
import com.Tom.uceva_dengue.Data.Model.PatientStateModel
import retrofit2.Response
import retrofit2.http.*

interface CaseEvolutionService {
    // ===== ESTADOS DE PACIENTE =====

    /**
     * Obtener todos los estados de paciente disponibles
     * (Ambulatorio, Observación, Hospitalizado, UCI, Recuperado, Fallecido)
     */
    @GET("PatientState")
    suspend fun getAllPatientStates(): Response<List<PatientStateModel>>

    /**
     * Obtener estado de paciente por ID
     */
    @GET("PatientState/{id}")
    suspend fun getPatientStateById(@Path("id") stateId: Int): Response<PatientStateModel>

    // ===== EVOLUCIÓN CLÍNICA =====

    /**
     * Crear nueva evolución clínica para un caso
     */
    @POST("Case/{caseId}/evolution")
    suspend fun createEvolution(
        @Path("caseId") caseId: Int,
        @Body evolutionData: CreateCaseEvolutionRequest
    ): Response<CaseEvolutionModel>

    /**
     * Obtener todas las evoluciones de un caso (historial completo)
     */
    @GET("Case/{caseId}/evolution")
    suspend fun getCaseEvolutions(@Path("caseId") caseId: Int): Response<List<CaseEvolutionModel>>

    /**
     * Obtener la última evolución de un caso
     */
    @GET("Case/{caseId}/evolution/latest")
    suspend fun getLatestEvolution(@Path("caseId") caseId: Int): Response<CaseEvolutionModel>

    /**
     * Obtener evolución por ID
     */
    @GET("Evolution/{id}")
    suspend fun getEvolutionById(@Path("id") evolutionId: Int): Response<CaseEvolutionModel>

    /**
     * Actualizar evolución
     */
    @PUT("Evolution/{id}")
    suspend fun updateEvolution(
        @Path("id") evolutionId: Int,
        @Body evolutionData: CreateCaseEvolutionRequest
    ): Response<CaseEvolutionModel>

    /**
     * Eliminar evolución
     */
    @DELETE("Evolution/{id}")
    suspend fun deleteEvolution(@Path("id") evolutionId: Int): Response<Map<String, String>>

    // ===== RESUMEN Y TENDENCIAS =====

    /**
     * Obtener resumen de evolución de un caso con tendencias
     * Incluye gráficos de plaquetas, hematocrito, signos de alarma, etc.
     */
    @GET("Case/{caseId}/evolution/summary")
    suspend fun getEvolutionSummary(@Path("caseId") caseId: Int): Response<CaseEvolutionSummaryModel>

    /**
     * Obtener evoluciones por día de enfermedad
     */
    @GET("Case/{caseId}/evolution/day/{day}")
    suspend fun getEvolutionsByDay(
        @Path("caseId") caseId: Int,
        @Path("day") day: Int
    ): Response<List<CaseEvolutionModel>>

    /**
     * Obtener casos con signos de alarma
     * Para alertas y seguimiento prioritario
     */
    @GET("Evolution/warning-signs")
    suspend fun getCasesWithWarningSigns(): Response<List<CaseEvolutionModel>>

    /**
     * Obtener casos que requieren atención inmediata
     */
    @GET("Evolution/urgent")
    suspend fun getUrgentCases(): Response<List<CaseEvolutionModel>>

    /**
     * Obtener evoluciones por estado del paciente
     */
    @GET("Evolution/by-state/{stateId}")
    suspend fun getEvolutionsByState(@Path("stateId") stateId: Int): Response<List<CaseEvolutionModel>>

    /**
     * Obtener historial de laboratorios de un caso (plaquetas, hematocrito, etc.)
     * Para gráficas de tendencia
     */
    @GET("Case/{caseId}/labs-history")
    suspend fun getLabsHistory(@Path("caseId") caseId: Int): Response<Map<String, List<Any>>>

    /**
     * Obtener historial de signos vitales
     */
    @GET("Case/{caseId}/vital-signs-history")
    suspend fun getVitalSignsHistory(@Path("caseId") caseId: Int): Response<Map<String, List<Any>>>
}
