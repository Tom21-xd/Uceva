package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseImportResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Servicio Retrofit para importación masiva de casos
 */
interface CaseImportService {

    /**
     * Importa casos desde un archivo CSV
     * POST /api/CaseImport/import-csv
     */
    @Multipart
    @POST("api/CaseImport/import-csv")
    suspend fun importCsv(
        @Part file: MultipartBody.Part,
        @Part("columnMapping") columnMapping: okhttp3.RequestBody?
    ): Response<CaseImportResponse>

    /**
     * Importa casos desde un archivo Excel
     * POST /api/CaseImport/import-excel
     */
    @Multipart
    @POST("api/CaseImport/import-excel")
    suspend fun importExcel(
        @Part file: MultipartBody.Part,
        @Part("columnMapping") columnMapping: okhttp3.RequestBody?
    ): Response<CaseImportResponse>

    /**
     * Descarga la plantilla CSV para importar casos
     * GET /api/CaseImport/download-template-csv
     */
    @GET("api/CaseImport/download-template-csv")
    @Streaming
    suspend fun downloadCsvTemplate(): Response<okhttp3.ResponseBody>

    /**
     * Descarga la plantilla Excel para importar casos
     * GET /api/CaseImport/download-template-excel
     */
    @GET("api/CaseImport/download-template-excel")
    @Streaming
    suspend fun downloadExcelTemplate(): Response<okhttp3.ResponseBody>

    /**
     * Actualiza las coordenadas de un caso específico
     * PATCH /Case/updateCoordinates/{id}
     */
    @PATCH("Case/updateCoordinates/{id}")
    suspend fun updateCaseCoordinates(
        @Path("id") caseId: Int,
        @Body coordinates: UpdateCoordinatesRequest
    ): Response<UpdateCoordinatesResponse>

    /**
     * Elimina un caso (eliminación lógica)
     * DELETE /Case/deleteCase/{id}
     */
    @DELETE("Case/deleteCase/{id}")
    suspend fun deleteCase(@Path("id") caseId: Int): Response<DeleteCaseResponse>
}

/**
 * Request body para actualizar coordenadas
 */
data class UpdateCoordinatesRequest(
    val latitude: Double,
    val longitude: Double
)

/**
 * Response de actualización de coordenadas
 */
data class UpdateCoordinatesResponse(
    val message: String,
    val caseId: Int,
    val latitude: Double,
    val longitude: Double
)

/**
 * Response de eliminación de caso
 */
data class DeleteCaseResponse(
    val message: String
)
