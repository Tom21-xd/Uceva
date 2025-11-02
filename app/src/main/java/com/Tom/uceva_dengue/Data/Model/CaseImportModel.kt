package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del endpoint de importación (envuelve el resultado)
 */
data class CaseImportResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: CaseImportResultDto
)

/**
 * Resultado de la importación de casos
 */
data class CaseImportResultDto(
    @SerializedName("totalRows")
    val totalRows: Int = 0,

    @SerializedName("successfulImports")
    val successfulImports: Int = 0,

    @SerializedName("failedImports")
    val failedImports: Int = 0,

    @SerializedName("errors")
    val errors: List<ImportErrorDto>? = null,

    @SerializedName("importedAt")
    val importedAt: String? = null,

    @SerializedName("importedByUserId")
    val importedByUserId: Int = 0,

    @SerializedName("processingTime")
    val processingTime: String? = null
)

/**
 * Error individual de importación
 */
data class ImportErrorDto(
    @SerializedName("rowNumber")
    val row: Int,

    @SerializedName("errorMessage")
    val error: String,

    @SerializedName("rowData")
    val rowData: Map<String, String?>? = null
)
