package com.Tom.uceva_dengue.Data.Service

import com.Tom.uceva_dengue.Data.Model.*
import retrofit2.Response
import retrofit2.http.*

interface QuizService {

    // Get all active quiz categories
    @GET("QuizControllerEF/categories")
    suspend fun getCategories(): Response<List<QuizCategoryModel>>

    // Start a new quiz attempt
    @POST("QuizControllerEF/start")
    suspend fun startQuiz(@Body request: StartQuizRequest): Response<QuizAttemptModel>

    // Submit an answer
    @POST("QuizControllerEF/answer")
    suspend fun submitAnswer(@Body request: SubmitAnswerRequest): Response<AnswerResultModel>

    // Submit/finish the quiz
    @POST("QuizControllerEF/submit")
    suspend fun submitQuiz(@Body request: SubmitQuizRequest): Response<QuizResultModel>

    // Get quiz result by attempt ID
    @GET("QuizControllerEF/result/{attemptId}")
    suspend fun getResult(@Path("attemptId") attemptId: Int): Response<QuizResultModel>

    // Get user's quiz history
    @GET("QuizControllerEF/history/{userId}")
    suspend fun getQuizHistory(@Path("userId") userId: Int): Response<List<QuizHistoryModel>>

    // Generate certificate (TEMPORALMENTE sin auth para debug)
    @POST("Certificate/generate/{userId}")
    suspend fun generateCertificate(@Path("userId") userId: Int): Response<CertificateModel>

    // Get certificate by ID
    @GET("Certificate/{id}")
    suspend fun getCertificate(@Path("id") certificateId: Int): Response<CertificateModel>

    // Get user's certificates
    @GET("Certificate/user/{userId}")
    suspend fun getUserCertificates(@Path("userId") userId: Int): Response<List<CertificateModel>>

    // Check certificate eligibility
    @GET("Certificate/eligible/{attemptId}")
    suspend fun checkEligibility(@Path("attemptId") attemptId: Int): Response<CertificateEligibilityModel>

    // Download certificate PDF
    @GET("Certificate/{id}/download")
    @Streaming
    suspend fun downloadCertificate(@Path("id") certificateId: Int): Response<okhttp3.ResponseBody>

    // Resend certificate by email
    @POST("Certificate/{id}/resend-email")
    suspend fun resendCertificateEmail(@Path("id") certificateId: Int): Response<ResendCertificateResponse>
}
