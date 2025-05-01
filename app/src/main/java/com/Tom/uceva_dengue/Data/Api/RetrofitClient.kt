package com.Tom.uceva_dengue.Data.Api

import com.Tom.uceva_dengue.Data.Service.AuthService
import com.Tom.uceva_dengue.Data.Service.CityService
import com.Tom.uceva_dengue.Data.Service.DepartmentService
import com.Tom.uceva_dengue.Data.Service.GenreService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://192.168.1.66:7051/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
    val departmentService: DepartmentService by lazy {
        retrofit.create(DepartmentService::class.java)
    }
    val genreService: GenreService by lazy {
        retrofit.create(GenreService::class.java)
    }
    val cityService: CityService by lazy {
        retrofit.create(CityService::class.java)
    }
}
