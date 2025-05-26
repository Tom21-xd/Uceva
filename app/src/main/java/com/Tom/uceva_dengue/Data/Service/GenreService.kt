package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.GenreModel
import retrofit2.Response
import retrofit2.http.GET

interface GenreService {
    @GET("Genre/getGenres")
    suspend fun getGenres(): Response<List<GenreModel>>
}