package com.Tom.uceva_dengue.Data.Api

import android.content.Context
import com.Tom.uceva_dengue.Data.Service.AuthRepository
import com.Tom.uceva_dengue.Data.Service.AuthService
import com.Tom.uceva_dengue.Data.Service.BloodTypeService
import com.Tom.uceva_dengue.Data.Service.CaseImportService
import com.Tom.uceva_dengue.Data.Service.CaseService
import com.Tom.uceva_dengue.Data.Service.CaseStateService
import com.Tom.uceva_dengue.Data.Service.CityService
import com.Tom.uceva_dengue.Data.Service.DengueService
import com.Tom.uceva_dengue.Data.Service.DepartmentService
import com.Tom.uceva_dengue.Data.Service.DiagnosticService
import com.Tom.uceva_dengue.Data.Service.FCMService
import com.Tom.uceva_dengue.Data.Service.GenreService
import com.Tom.uceva_dengue.Data.Service.HospitalService
import com.Tom.uceva_dengue.Data.Service.NotificationService
import com.Tom.uceva_dengue.Data.Service.PermissionService
import com.Tom.uceva_dengue.Data.Service.PublicationService
import com.Tom.uceva_dengue.Data.Service.PublicationCategoryService
import com.Tom.uceva_dengue.Data.Service.PublicationTagService
import com.Tom.uceva_dengue.Data.Service.QuizService
import com.Tom.uceva_dengue.Data.Service.RoleService
import com.Tom.uceva_dengue.Data.Service.StatisticsService
import com.Tom.uceva_dengue.Data.Service.SymptomService
import com.Tom.uceva_dengue.Data.Service.UserService
import com.Tom.uceva_dengue.Data.Service.UserApprovalService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://api.prometeondev.com/"

    lateinit var context: Context
        private set

    /**
     * Initialize RetrofitClient with application context
     * Call this from your Application class or MainActivity
     */
    fun initialize(appContext: Context) {
        context = appContext.applicationContext
        android.util.Log.d("RetrofitClient", "Initialized with context")
    }

    /**
     * Auth interceptor that adds Bearer token to all requests
     * and handles token refresh on 401 errors
     */
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()

        // Skip auth for login/register/refresh endpoints
        val skipAuth = original.url.encodedPath.contains("/Auth/login") ||
                      original.url.encodedPath.contains("/Auth/register") ||
                      original.url.encodedPath.contains("/Auth/refresh")

        if (skipAuth) {
            android.util.Log.d("RetrofitClient", "Skipping auth for: ${original.url}")
            return@Interceptor chain.proceed(original)
        }

        android.util.Log.d("RetrofitClient", "Interceptor for: ${original.url}")

        // Get token
        val token = try {
            if (::context.isInitialized) {
                val repo = AuthRepository(context)
                val accessToken = repo.getAccessToken()
                android.util.Log.d("RetrofitClient", "Token exists: ${!accessToken.isNullOrEmpty()}")
                if (!accessToken.isNullOrEmpty()) {
                    android.util.Log.d("RetrofitClient", "Token preview: ${accessToken.take(30)}...")
                }
                accessToken
            } else {
                android.util.Log.e("RetrofitClient", "Context not initialized!")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("RetrofitClient", "Error getting token: ${e.message}")
            null
        }

        // Build request with or without token
        val newRequest = if (!token.isNullOrEmpty()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            android.util.Log.w("RetrofitClient", "No token available, proceeding without auth")
            original
        }

        // Execute request
        val response = chain.proceed(newRequest)

        // Handle 401 Unauthorized - try to refresh token
        if (response.code == 401 && ::context.isInitialized) {
            android.util.Log.w("RetrofitClient", "Received 401, attempting token refresh")
            response.close()

            try {
                val repo = AuthRepository(context)
                val refreshToken = repo.getRefreshToken()

                if (!refreshToken.isNullOrEmpty()) {
                    android.util.Log.d("RetrofitClient", "Refresh token available, refreshing...")

                    // Attempt to refresh token synchronously
                    val refreshResponse = kotlinx.coroutines.runBlocking {
                        authService.refreshToken(
                            com.Tom.uceva_dengue.Data.Model.RefreshTokenRequest(refreshToken)
                        )
                    }

                    if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                        val newAccessToken = refreshResponse.body()!!.accessToken
                        android.util.Log.d("RetrofitClient", "Token refreshed successfully")

                        // Save new access token
                        kotlinx.coroutines.runBlocking {
                            repo.saveAccessToken(newAccessToken)
                        }

                        // Retry original request with new token
                        val retryRequest = original.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()

                        return@Interceptor chain.proceed(retryRequest)
                    } else {
                        android.util.Log.e("RetrofitClient", "Token refresh failed: ${refreshResponse.code()}")
                        // Clear tokens and let the app handle re-login
                        kotlinx.coroutines.runBlocking {
                            repo.clearSession()
                        }
                    }
                } else {
                    android.util.Log.w("RetrofitClient", "No refresh token available")
                }
            } catch (e: Exception) {
                android.util.Log.e("RetrofitClient", "Error during token refresh: ${e.message}")
            }
        }

        response
    }

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // Timeout de conexión: 60 segundos
            .readTimeout(60, TimeUnit.SECONDS)     // Timeout de lectura: 60 segundos
            .writeTimeout(60, TimeUnit.SECONDS)    // Timeout de escritura: 60 segundos
            .addInterceptor(authInterceptor)       // Auth interceptor PRIMERO
            .addInterceptor(loggingInterceptor)    // Logging para debug
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)  // Usar el cliente con timeouts
            .addConverterFactory(ScalarsConverterFactory.create())  // Primero Scalars
            .addConverterFactory(GsonConverterFactory.create())  // Para respuestas en texto
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
    val bloodTypeService: BloodTypeService by lazy {
        retrofit.create(BloodTypeService::class.java)
    }
    val publicationService : PublicationService by lazy {
        retrofit.create(PublicationService::class.java)
    }
    val caseService : CaseService by lazy {
        retrofit.create(CaseService::class.java)
    }
    val userService : UserService by lazy {
        retrofit.create(UserService::class.java)
    }
    val dengueService : DengueService by lazy {
        retrofit.create(DengueService::class.java)
    }
    val hospitalService : HospitalService by lazy {
        retrofit.create(HospitalService::class.java)
    }
    val notificationService : NotificationService by lazy {
        retrofit.create(NotificationService::class.java)
    }
    val diagnosticService : DiagnosticService by lazy {
        retrofit.create(DiagnosticService::class.java)
    }
    val statisticsService : StatisticsService by lazy {
        retrofit.create(StatisticsService::class.java)
    }
    val fcmService : FCMService by lazy {
        retrofit.create(FCMService::class.java)
    }
    val publicationCategoryService : PublicationCategoryService by lazy {
        retrofit.create(PublicationCategoryService::class.java)
    }
    val publicationTagService : PublicationTagService by lazy {
        retrofit.create(PublicationTagService::class.java)
    }
    val caseStateService : CaseStateService by lazy {
        retrofit.create(CaseStateService::class.java)
    }
    val roleService : RoleService by lazy {
        retrofit.create(RoleService::class.java)
    }
    val symptomService : SymptomService by lazy {
        retrofit.create(SymptomService::class.java)
    }
    val quizService : QuizService by lazy {
        retrofit.create(QuizService::class.java)
    }
    val permissionService : PermissionService by lazy {
        retrofit.create(PermissionService::class.java)
    }
    val caseImportService : CaseImportService by lazy {
        retrofit.create(CaseImportService::class.java)
    }
    val userApprovalService : UserApprovalService by lazy {
        retrofit.create(UserApprovalService::class.java)
    }
}
