    package com.Tom.uceva_dengue.Data.Service

    import com.Tom.uceva_dengue.Data.Model.BloodTypeModel
    import com.Tom.uceva_dengue.Data.Model.CaseStateModel
    import com.Tom.uceva_dengue.Data.Model.SymptomModel
    import com.Tom.uceva_dengue.Data.Model.TypeOfDengueModel
    import retrofit2.Response
    import retrofit2.http.GET

    interface DengueService {

        @GET("Dengue/getSymptoms")
        suspend fun getSymptoms(): Response<List<SymptomModel>>

        @GET("Dengue/getTypesOfDengue")
        suspend fun getTypesOfDengue(): Response<List<TypeOfDengueModel>>

        @GET("Dengue/getTypesOfBlood")
        suspend fun getTypesOfBlood(): Response<List<BloodTypeModel>>

    }
