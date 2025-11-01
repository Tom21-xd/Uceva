package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Resultado de la importación de casos
 */
data class CaseImportResultDto(
    @SerializedName("totalRows")
    val totalRows: Int,

    @SerializedName("successfulImports")
    val successfulImports: Int,

    @SerializedName("failedImports")
    val failedImports: Int,

    @SerializedName("errors")
    val errors: List<ImportErrorDto>,

    @SerializedName("importedAt")
    val importedAt: String,

    @SerializedName("importedBy")
    val importedBy: Int,

    @SerializedName("message")
    val message: String
)

/**
 * Error individual de importación
 */
data class ImportErrorDto(
    @SerializedName("row")
    val row: Int,

    @SerializedName("field")
    val field: String?,

    @SerializedName("error")
    val error: String
)
