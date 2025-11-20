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
    val processingTime: String? = null,

    @SerializedName("importedCases")
    val importedCases: List<ImportedCaseDto>? = null
)

/**
 * DTO para representar un caso importado con sus coordenadas
 */
@Parcelize
data class ImportedCaseDto(
    @SerializedName("caseId")
    val caseId: Int,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("neighborhood")
    val neighborhood: String?,

    @SerializedName("temporaryName")
    val temporaryName: String?,

    @SerializedName("year")
    val year: Int?,

    @SerializedName("age")
    val age: Int?,

    @SerializedName("dengueType")
    val dengueType: String?
) : Parcelable

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
