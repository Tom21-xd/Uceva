package com.Tom.uceva_dengue.Data.Model

data class DiagnosisRequest(
    val symptomIds: List<Int>
)

data class DiagnosisResponse(
    val mostLikelyDiagnosis: MostLikelyDiagnosis,
    val allResults: List<DiagnosisResult>,
    val disclaimer: String
)

data class MostLikelyDiagnosis(
    val typeOfDengueId: Int,
    val typeOfDengueName: String,
    val matchPercentage: Double,
    val confidence: String
)

data class DiagnosisResult(
    val typeOfDengueId: Int,
    val typeOfDengueName: String,
    val matchingSymptoms: Int,
    val totalSymptomsInType: Int,
    val totalSymptomsProvided: Int,
    val matchPercentage: Double,
    val precisionPercentage: Double,
    val recallPercentage: Double
)
