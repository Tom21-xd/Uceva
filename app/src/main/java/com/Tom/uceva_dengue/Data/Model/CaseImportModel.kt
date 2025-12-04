package com.Tom.uceva_dengue.Data.Model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

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
 * Backend usa PascalCase (PropertyNamingPolicy = null)
 */
data class CaseImportResultDto(
    @SerializedName("TotalRows")
    val totalRows: Int = 0,

    @SerializedName("SuccessfulImports")
    val successfulImports: Int = 0,

    @SerializedName("FailedImports")
    val failedImports: Int = 0,

    @SerializedName("Errors")
    val errors: List<ImportErrorDto>? = null,

    @SerializedName("ImportedAt")
    val importedAt: String? = null,

    @SerializedName("ImportedByUserId")
    val importedByUserId: Int = 0,

    @SerializedName("ProcessingTime")
    val processingTime: String? = null,

    @SerializedName("ImportedCases")
    val importedCases: List<ImportedCaseDto>? = null
)

/**
 * DTO para representar un caso importado con sus coordenadas
 * Backend usa PascalCase (PropertyNamingPolicy = null)
 */
@Parcelize
data class ImportedCaseDto(
    @SerializedName("CaseId")
    val caseId: Int,

    @SerializedName("Latitude")
    val latitude: Double?,

    @SerializedName("Longitude")
    val longitude: Double?,

    @SerializedName("Neighborhood")
    val neighborhood: String?,

    @SerializedName("TemporaryName")
    val temporaryName: String?,

    @SerializedName("Year")
    val year: Int?,

    @SerializedName("Age")
    val age: Int?,

    @SerializedName("DengueType")
    val dengueType: String?
) : Parcelable

/**
 * Error individual de importación
 * Backend usa PascalCase (PropertyNamingPolicy = null)
 */
data class ImportErrorDto(
    @SerializedName("RowNumber")
    val row: Int,

    @SerializedName("ErrorMessage")
    val error: String,

    @SerializedName("RowData")
    val rowData: Map<String, String?>? = null
)
