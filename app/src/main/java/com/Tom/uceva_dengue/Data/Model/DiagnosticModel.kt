package com.Tom.uceva_dengue.Data.Model

data class DiagnosticRequest(
    val sintomas_ids: List<Int>
)

data class DiagnosticResult(
    val ID_TIPODENGUE: Int,
    val NOMBRE_TIPODENGUE: String,
    val puntaje: Int,
    val sintomas_coincidentes: Int,
    val total_sintomas: Int,
    val porcentaje_coincidencia: Double,
    val diagnostico: String
)

data class DiagnosticResponse(
    val message: String,
    val sintomas_evaluados: Int,
    val resultados: List<DiagnosticResult>,
    val recomendacion: String
)
