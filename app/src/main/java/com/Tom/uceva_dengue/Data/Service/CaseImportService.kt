package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.CaseImportResultDto
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
        @Part file: MultipartBody.Part
    ): Response<CaseImportResultDto>

    /**
     * Importa casos desde un archivo Excel
     * POST /api/CaseImport/import-excel
     */
    @Multipart
    @POST("api/CaseImport/import-excel")
    suspend fun importExcel(
        @Part file: MultipartBody.Part
    ): Response<CaseImportResultDto>

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
}
