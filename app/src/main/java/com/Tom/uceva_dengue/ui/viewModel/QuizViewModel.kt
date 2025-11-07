package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel : ViewModel() {
    private val quizService = RetrofitClient.quizService

    // Categories
    private val _categories = MutableStateFlow<List<QuizCategoryModel>>(emptyList())
    val categories: StateFlow<List<QuizCategoryModel>> = _categories.asStateFlow()

    // Current quiz attempt
    private val _currentAttempt = MutableStateFlow<QuizAttemptModel?>(null)
    val currentAttempt: StateFlow<QuizAttemptModel?> = _currentAttempt.asStateFlow()

    // Current question index
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // Selected answer for current question
    private val _selectedAnswer = MutableStateFlow<Int?>(null)
    val selectedAnswer: StateFlow<Int?> = _selectedAnswer.asStateFlow()

    // Answer result (feedback after answering)
    private val _answerResult = MutableStateFlow<AnswerResultModel?>(null)
    val answerResult: StateFlow<AnswerResultModel?> = _answerResult.asStateFlow()

    // Quiz result
    private val _quizResult = MutableStateFlow<QuizResultModel?>(null)
    val quizResult: StateFlow<QuizResultModel?> = _quizResult.asStateFlow()

    // Quiz history
    private val _quizHistory = MutableStateFlow<List<QuizHistoryModel>>(emptyList())
    val quizHistory: StateFlow<List<QuizHistoryModel>> = _quizHistory.asStateFlow()

    // Certificate
    private val _certificate = MutableStateFlow<CertificateModel?>(null)
    val certificate: StateFlow<CertificateModel?> = _certificate.asStateFlow()

    // Certificate eligibility
    private val _eligibility = MutableStateFlow<CertificateEligibilityModel?>(null)
    val eligibility: StateFlow<CertificateEligibilityModel?> = _eligibility.asStateFlow()

    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Timer
    private val _elapsedTime = MutableStateFlow(0)
    val elapsedTime: StateFlow<Int> = _elapsedTime.asStateFlow()

    private val _questionStartTime = MutableStateFlow(0L)

    init {
        loadCategories()
    }

    // Load quiz categories
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = quizService.getCategories()
                if (response.isSuccessful) {
                    _categories.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar categorías: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading categories", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Start new quiz
    fun startQuiz(userId: Int, totalQuestions: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("QuizViewModel", "Iniciando quiz para usuario: $userId, preguntas: $totalQuestions")
                val request = StartQuizRequest(userId, totalQuestions)
                val response = quizService.startQuiz(request)

                Log.d("QuizViewModel", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val attempt = response.body()
                    Log.d("QuizViewModel", "Attempt recibido: ${attempt?.id}, Preguntas: ${attempt?.questions?.size}")

                    if (attempt != null) {
                        _currentAttempt.value = attempt
                        _currentQuestionIndex.value = 0
                        _selectedAnswer.value = null
                        _answerResult.value = null
                        _elapsedTime.value = 0
                        _questionStartTime.value = System.currentTimeMillis()

                        Log.d("QuizViewModel", "Quiz iniciado correctamente con ${attempt.questions.size} preguntas")
                    } else {
                        _errorMessage.value = "El servidor no devolvió datos del quiz"
                        Log.e("QuizViewModel", "Response body es null")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuizViewModel", "Error ${response.code()}: $errorBody")
                    _errorMessage.value = "Error al iniciar quiz: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error starting quiz", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Select an answer
    fun selectAnswer(answerId: Int) {
        _selectedAnswer.value = answerId
    }

    // Submit current answer
    fun submitAnswer() {
        val attemptId = _currentAttempt.value?.id ?: return
        val questionId = _currentAttempt.value?.questions?.get(_currentQuestionIndex.value)?.id ?: return
        val answerId = _selectedAnswer.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val timeSpent = ((System.currentTimeMillis() - _questionStartTime.value) / 1000).toInt()
                val request = SubmitAnswerRequest(attemptId, questionId, answerId, timeSpent)
                val response = quizService.submitAnswer(request)

                if (response.isSuccessful) {
                    _answerResult.value = response.body()
                } else {
                    _errorMessage.value = "Error al enviar respuesta: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error submitting answer", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Go to next question
    fun nextQuestion() {
        val totalQuestions = _currentAttempt.value?.questions?.size ?: 0
        if (_currentQuestionIndex.value < totalQuestions - 1) {
            _currentQuestionIndex.value += 1
            _selectedAnswer.value = null
            _answerResult.value = null
            _questionStartTime.value = System.currentTimeMillis()
        }
    }

    // Submit/finish quiz
    fun finishQuiz() {
        val attemptId = _currentAttempt.value?.id ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val request = SubmitQuizRequest(attemptId, _elapsedTime.value)
                val response = quizService.submitQuiz(request)

                if (response.isSuccessful) {
                    _quizResult.value = response.body()
                    // Check certificate eligibility
                    checkCertificateEligibility(attemptId)
                } else {
                    _errorMessage.value = "Error al finalizar quiz: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error finishing quiz", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get quiz history
    fun loadQuizHistory(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = quizService.getQuizHistory(userId)
                if (response.isSuccessful) {
                    _quizHistory.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar historial: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading history", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Generate certificate (TEMPORALMENTE usando userId directo para debug)
    fun generateCertificate(userId: Int = 1) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("QuizViewModel", "=== GENERATING CERTIFICATE ===")
                Log.d("QuizViewModel", "UserId: $userId")

                val response = quizService.generateCertificate(userId)
                Log.d("QuizViewModel", "Response code: ${response.code()}")
                Log.d("QuizViewModel", "Response message: ${response.message()}")
                Log.d("QuizViewModel", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("QuizViewModel", "Response body: $body")
                    Log.d("QuizViewModel", "Certificate ID: ${body?.id}")
                    Log.d("QuizViewModel", "Certificate Status: ${body?.status}")
                    Log.d("QuizViewModel", "Certificate Score: ${body?.score}")
                    _certificate.value = body
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuizViewModel", "Error code: ${response.code()}")
                    Log.e("QuizViewModel", "Error message: ${response.message()}")
                    Log.e("QuizViewModel", "Error body: $errorBody")
                    _errorMessage.value = "Error al generar certificado: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Exception in generateCertificate", e)
                Log.e("QuizViewModel", "Exception type: ${e.javaClass.name}")
                Log.e("QuizViewModel", "Exception message: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("QuizViewModel", "=== CERTIFICATE GENERATION COMPLETE ===")
            }
        }
    }

    // Check certificate eligibility
    private fun checkCertificateEligibility(attemptId: Int) {
        viewModelScope.launch {
            try {
                Log.d("QuizViewModel", "=== CHECKING CERTIFICATE ELIGIBILITY ===")
                Log.d("QuizViewModel", "Attempt ID: $attemptId")

                val response = quizService.checkEligibility(attemptId)
                Log.d("QuizViewModel", "Eligibility response code: ${response.code()}")
                Log.d("QuizViewModel", "Eligibility response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val eligibility = response.body()
                    Log.d("QuizViewModel", "Eligibility: ${eligibility?.isEligible}")
                    Log.d("QuizViewModel", "Score: ${eligibility?.score}")
                    Log.d("QuizViewModel", "Required Score: ${eligibility?.requiredScore}")
                    _eligibility.value = eligibility
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuizViewModel", "Eligibility error code: ${response.code()}")
                    Log.e("QuizViewModel", "Eligibility error body: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Exception in checkCertificateEligibility", e)
                Log.e("QuizViewModel", "Exception message: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
            }
        }
    }

    // Load user certificates
    fun loadUserCertificates(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                Log.d("QuizViewModel", "=== LOADING USER CERTIFICATES ===")
                Log.d("QuizViewModel", "User ID: $userId")

                val response = quizService.getUserCertificates(userId)
                Log.d("QuizViewModel", "Response code: ${response.code()}")
                Log.d("QuizViewModel", "Response message: ${response.message()}")
                Log.d("QuizViewModel", "Response successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val certificates = response.body() ?: emptyList()
                    Log.d("QuizViewModel", "Certificates received: ${certificates.size}")
                    certificates.forEachIndexed { index, cert ->
                        Log.d("QuizViewModel", "Certificate $index - ID: ${cert.id}, Status: ${cert.status}, Score: ${cert.score}")
                    }

                    // Filter to get only the active certificate (one per user policy)
                    val activeCert = certificates.firstOrNull { it.status == "Active" }
                    Log.d("QuizViewModel", "Active certificate: ${activeCert?.id}")
                    _certificate.value = activeCert
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuizViewModel", "Error code: ${response.code()}")
                    Log.e("QuizViewModel", "Error message: ${response.message()}")
                    Log.e("QuizViewModel", "Error body: $errorBody")
                    _errorMessage.value = "Error al cargar certificado: ${response.code()} - $errorBody"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Exception in loadUserCertificates", e)
                Log.e("QuizViewModel", "Exception type: ${e.javaClass.name}")
                Log.e("QuizViewModel", "Exception message: ${e.message}")
                Log.e("QuizViewModel", "Stack trace: ${e.stackTraceToString()}")
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d("QuizViewModel", "=== CERTIFICATE LOADING COMPLETE ===")
            }
        }
    }

    // Resend certificate by email
    fun resendCertificateEmail(certificateId: Int, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("QuizViewModel", "Reenviando certificado $certificateId por email")
                val response = quizService.resendCertificateEmail(certificateId)

                if (response.isSuccessful) {
                    val result = response.body()
                    if (result != null) {
                        Log.d("QuizViewModel", "Certificado reenviado a: ${result.email}")
                        onSuccess(result.message)
                    } else {
                        onError("Error: respuesta vacía del servidor")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("QuizViewModel", "Error ${response.code()}: $errorBody")
                    onError("Error al reenviar certificado: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error resending certificate email", e)
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    // Update elapsed time (called from UI timer)
    fun updateElapsedTime(seconds: Int) {
        _elapsedTime.value = seconds
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Reset quiz state
    fun resetQuiz() {
        _currentAttempt.value = null
        _currentQuestionIndex.value = 0
        _selectedAnswer.value = null
        _answerResult.value = null
        _quizResult.value = null
        _elapsedTime.value = 0
        _errorMessage.value = null
    }
}
