package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.HospitalModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface HospitalService {

    @GET("Hospital/getHospitals")
    suspend fun getHospitals(): Response<List<HospitalModel>>

    @GET("Hospital/filterHospitals")
    suspend fun filterHospitals(@Query("name") name: String): Response<List<HospitalModel>>

    @GET("Hospital/getHospitaToCity")
    suspend fun getHospitalsByCity(@Query("filtro") cityId: Int): Response<List<HospitalModel>>

    // HU-009: Obtener hospital por ID
    @GET("Hospital/getHospitalById/{id}")
    suspend fun getHospitalById(@Path("id") id: Int): Response<HospitalModel>

    // HU-009: Crear hospital
    @Multipart
    @POST("Hospital/createHospital")
    suspend fun createHospital(
        @Part("nombre") nombre: RequestBody,
        @Part("direccion") direccion: RequestBody,
        @Part("latitud") latitud: RequestBody,
        @Part("longitud") longitud: RequestBody,
        @Part("id_municipio") idMunicipio: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Map<String, String>>

    // HU-009: Actualizar hospital
    @PUT("Hospital/updateHospital/{id}")
    suspend fun updateHospital(
        @Path("id") id: Int,
        @Body hospitalData: Map<String, Any?>
    ): Response<Map<String, String>>

    // HU-009: Eliminar hospital (soft delete)
    @DELETE("Hospital/deleteHospital/{id}")
    suspend fun deleteHospital(@Path("id") id: Int): Response<Map<String, String>>
}
