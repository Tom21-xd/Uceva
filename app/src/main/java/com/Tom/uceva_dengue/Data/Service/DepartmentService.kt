package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.DepartmentModel
import com.Tom.uceva_dengue.Data.Model.UserModel
import retrofit2.http.GET

interface DepartmentService {
    @GET("Department/getDepartments")
    suspend fun getDepartments(): List<DepartmentModel>
}