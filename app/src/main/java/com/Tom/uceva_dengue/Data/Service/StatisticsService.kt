package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface StatisticsService {
    // HU-008: Estadísticas generales
    @GET("Statistics/general")
    suspend fun getGeneralStatistics(): Response<GeneralStats>

    // HU-008: Casos por tipo de dengue
    @GET("Statistics/byDengueType")
    suspend fun getCasesByDengueType(): Response<List<DengueTypeStats>>

    // HU-008: Casos por mes
    @GET("Statistics/byMonth")
    suspend fun getCasesByMonth(@Query("year") year: Int?): Response<List<MonthlyStats>>

    // HU-008: Casos por departamento
    @GET("Statistics/byDepartment")
    suspend fun getCasesByDepartment(): Response<List<DepartmentStats>>

    // HU-008: Tendencia de casos
    @GET("Statistics/trends")
    suspend fun getCaseTrends(@Query("months") months: Int?): Response<List<TrendStats>>

    // HU-008: Top hospitales por casos
    @GET("Statistics/topHospitals")
    suspend fun getTopHospitals(@Query("limit") limit: Int?): Response<List<TopHospitalStats>>

    // Casos para mapa con filtro por año
    @GET("Statistics/mapCases")
    suspend fun getMapCases(@Query("year") year: Int?): Response<List<MapCase>>

    // Años disponibles para filtrar
    @GET("Statistics/availableYears")
    suspend fun getAvailableYears(): Response<List<Int>>
}
