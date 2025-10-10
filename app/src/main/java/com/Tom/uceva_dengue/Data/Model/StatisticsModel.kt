package com.Tom.uceva_dengue.Data.Model

// Estadísticas generales
data class GeneralStats(
    val total_casos: Int,
    val casos_activos: Int,
    val casos_recuperados: Int,
    val fallecidos: Int,
    val total_hospitales: Int,
    val hospitales_activos: Int,
    val total_usuarios: Int,
    val usuarios_sanos: Int,
    val usuarios_enfermos: Int,
    val total_publicaciones: Int
)

// Casos por tipo de dengue
data class DengueTypeStats(
    val ID_TIPODENGUE: Int,
    val NOMBRE_TIPODENGUE: String,
    val total_casos: Int,
    val casos_activos: Int,
    val casos_recuperados: Int,
    val fallecidos: Int,
    val porcentaje_del_total: Double
)

// Casos por mes
data class MonthlyStats(
    val anio: Int,
    val mes: Int,
    val nombre_mes: String,
    val total_casos: Int,
    val casos_activos: Int,
    val casos_recuperados: Int,
    val fallecidos: Int
)

// Casos por departamento
data class DepartmentStats(
    val ID_DEPARTAMENTO: Int,
    val NOMBRE_DEPARTAMENTO: String,
    val total_casos: Int,
    val casos_activos: Int,
    val casos_recuperados: Int,
    val fallecidos: Int,
    val porcentaje_del_total: Double
)

// Tendencia de casos
data class TrendStats(
    val periodo: String,
    val total_casos: Int,
    val nuevos_casos: Int,
    val recuperados: Int,
    val fallecidos: Int,
    val variacion_porcentual: Double?
)

// Top hospitales
data class TopHospitalStats(
    val ID_HOSPITAL: Int,
    val NOMBRE_HOSPITAL: String,
    val municipio: String,
    val departamento: String,
    val total_casos: Int,
    val casos_activos: Int,
    val casos_recuperados: Int,
    val fallecidos: Int,
    val porcentaje_del_total: Double
)
