package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

// Quiz Category
data class QuizCategoryModel(
    @SerializedName("ID_CATEGORIA") val id: Int,
    @SerializedName("NOMBRE_CATEGORIA") val name: String,
    @SerializedName("DESCRIPCION") val description: String?,
    @SerializedName("ICONO") val icon: String?,
    @SerializedName("ORDEN_VISUALIZACION") val displayOrder: Int,
    @SerializedName("ESTA_ACTIVA") val isActive: Boolean,
    @SerializedName("TOTAL_PREGUNTAS") val totalQuestions: Int
)

// Quiz Question
data class QuizQuestionModel(
    @SerializedName("ID_PREGUNTA") val id: Int,
    @SerializedName("FK_ID_CATEGORIA") val categoryId: Int,
    @SerializedName("NOMBRE_CATEGORIA") val categoryName: String?,
    @SerializedName("TEXTO_PREGUNTA") val questionText: String,
    @SerializedName("TIPO_PREGUNTA") val questionType: String,
    @SerializedName("DIFICULTAD") val difficulty: Int,
    @SerializedName("PUNTOS") val points: Int,
    @SerializedName("EXPLICACION_RESPUESTA") val explanationText: String?,
    @SerializedName("ESTA_ACTIVA") val isActive: Boolean,
    @SerializedName("RESPUESTAS") val answers: List<QuizAnswerModel>
)

// Quiz Answer
data class QuizAnswerModel(
    @SerializedName("ID_RESPUESTA") val id: Int,
    @SerializedName("FK_ID_PREGUNTA") val questionId: Int,
    @SerializedName("TEXTO_RESPUESTA") val answerText: String,
    @SerializedName("ES_CORRECTA") val isCorrect: Boolean?,
    @SerializedName("ORDEN_RESPUESTA") val displayOrder: Int
)

// Start Quiz Request
data class StartQuizRequest(
    @SerializedName("ID_USUARIO") val userId: Int,
    @SerializedName("TOTAL_PREGUNTAS") val totalQuestions: Int = 10
)

// Quiz Attempt
data class QuizAttemptModel(
    @SerializedName("ID_INTENTO") val id: Int,
    @SerializedName("FK_ID_USUARIO") val userId: Int,
    @SerializedName("FECHA_INICIO") val startedAt: String,
    @SerializedName("PREGUNTAS") val questions: List<QuizQuestionModel>,
    @SerializedName("TOTAL_PREGUNTAS") val totalQuestions: Int,
    @SerializedName("ESTADO_INTENTO") val status: String
)

// Submit Answer Request
data class SubmitAnswerRequest(
    @SerializedName("ID_INTENTO") val attemptId: Int,
    @SerializedName("ID_PREGUNTA") val questionId: Int,
    @SerializedName("ID_RESPUESTA_SELECCIONADA") val selectedAnswerId: Int,
    @SerializedName("TIEMPO_RESPUESTA_SEGUNDOS") val timeSpentSeconds: Int
)

// Answer Result
data class AnswerResultModel(
    @SerializedName("ES_CORRECTA") val isCorrect: Boolean,
    @SerializedName("RESPUESTA_CORRECTA_ID") val correctAnswerId: Int,
    @SerializedName("EXPLICACION") val explanation: String?
)

// Submit Quiz Request
data class SubmitQuizRequest(
    @SerializedName("ID_INTENTO") val attemptId: Int,
    @SerializedName("TIEMPO_TOTAL_SEGUNDOS") val totalTimeSeconds: Int
)

// Quiz Result
data class QuizResultModel(
    @SerializedName("ID_INTENTO") val attemptId: Int,
    @SerializedName("PUNTUACION_OBTENIDA") val score: Double,
    @SerializedName("TOTAL_PREGUNTAS") val totalQuestions: Int,
    @SerializedName("RESPUESTAS_CORRECTAS") val correctAnswers: Int,
    @SerializedName("RESPUESTAS_INCORRECTAS") val incorrectAnswers: Int,
    @SerializedName("TIEMPO_TOTAL_SEGUNDOS") val totalTimeSeconds: Int,
    @SerializedName("FECHA_FINALIZACION") val completedAt: String,
    @SerializedName("APROBADO") val passed: Boolean,
    @SerializedName("PUEDE_GENERAR_CERTIFICADO") val canGenerateCertificate: Boolean,
    @SerializedName("DETALLES_RESPUESTAS") val answerDetails: List<QuizAnswerDetailModel>
)

// Quiz Answer Detail
data class QuizAnswerDetailModel(
    @SerializedName("PREGUNTA") val questionText: String,
    @SerializedName("RESPUESTA_USUARIO") val userAnswer: String,
    @SerializedName("RESPUESTA_CORRECTA") val correctAnswer: String,
    @SerializedName("ES_CORRECTA") val isCorrect: Boolean,
    @SerializedName("EXPLICACION") val explanation: String?
)

// Quiz History
data class QuizHistoryModel(
    @SerializedName("ID_INTENTO") val attemptId: Int,
    @SerializedName("FECHA_INICIO") val startedAt: String,
    @SerializedName("FECHA_FINALIZACION") val completedAt: String?,
    @SerializedName("PUNTUACION_OBTENIDA") val score: Double,
    @SerializedName("TOTAL_PREGUNTAS") val totalQuestions: Int,
    @SerializedName("RESPUESTAS_CORRECTAS") val correctAnswers: Int,
    @SerializedName("ESTADO_INTENTO") val status: String,
    @SerializedName("APROBADO") val passed: Boolean,
    @SerializedName("TIENE_CERTIFICADO") val hasCertificate: Boolean
)

// Certificate
data class CertificateModel(
    @SerializedName("ID_CERTIFICADO") val id: Int,
    @SerializedName("CODIGO_VERIFICACION") val verificationCode: String,
    @SerializedName("FECHA_EMISION") val issuedAt: String,
    @SerializedName("PUNTUACION_OBTENIDA") val score: Double,
    @SerializedName("NOMBRE_USUARIO") val userName: String,
    @SerializedName("CORREO_USUARIO") val userEmail: String,
    @SerializedName("URL_PDF_CERTIFICADO") val pdfUrl: String?,
    @SerializedName("ESTADO_CERTIFICADO") val status: String
)

// Generate Certificate Request
data class GenerateCertificateRequest(
    @SerializedName("AttemptId") val attemptId: Int
)

// Certificate Eligibility
data class CertificateEligibilityModel(
    @SerializedName("IsEligible") val isEligible: Boolean,
    @SerializedName("AttemptId") val attemptId: Int,
    @SerializedName("Score") val score: Double,
    @SerializedName("RequiredScore") val requiredScore: Double,
    @SerializedName("IsCompleted") val isCompleted: Boolean,
    @SerializedName("HasExistingCertificate") val hasExistingCertificate: Boolean,
    @SerializedName("ExistingCertificateId") val existingCertificateId: Int?,
    @SerializedName("Message") val message: String
)

// Resend Certificate Response
data class ResendCertificateResponse(
    @SerializedName("message") val message: String,
    @SerializedName("email") val email: String,
    @SerializedName("certificateId") val certificateId: Int
)
