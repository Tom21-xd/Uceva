    package com.Tom.uceva_dengue.Data.Service

    import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
    import com.Tom.uceva_dengue.Data.Model.CaseStateModel
    import com.Tom.uceva_dengue.Data.Model.DiagnosisRequest
    import com.Tom.uceva_dengue.Data.Model.DiagnosisResponse
    import com.Tom.uceva_dengue.Data.Model.SymptomModel
    import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
    import retrofit2.Response
    import retrofit2.http.Body
    import retrofit2.http.GET
    import retrofit2.http.POST

    interface DengueService {

        @GET("Symptom/getSymptoms")
        suspend fun getSymptoms(): Response<List<SymptomModel>>

        @GET("Dengue/getTypesOfDengue")
        suspend fun getTypesOfDengue(): Response<List<TypeOfDengueModel>>

        @GET("Dengue/getTypesOfBlood")
        suspend fun getTypesOfBlood(): Response<List<BloodTypeModel>>

        @POST("Dengue/diagnose")
        suspend fun diagnoseDengue(@Body symptomIds: List<Int>): Response<DiagnosisResponse>

    }
