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

    // Generate certificate
    fun generateCertificate(attemptId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val request = GenerateCertificateRequest(attemptId)
                val response = quizService.generateCertificate(request)

                if (response.isSuccessful) {
                    _certificate.value = response.body()
                } else {
                    _errorMessage.value = "Error al generar certificado: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error generating certificate", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Check certificate eligibility
    private fun checkCertificateEligibility(attemptId: Int) {
        viewModelScope.launch {
            try {
                val response = quizService.checkEligibility(attemptId)
                if (response.isSuccessful) {
                    _eligibility.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error checking eligibility", e)
            }
        }
    }

    // Load user certificates
    fun loadUserCertificates(userId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = quizService.getUserCertificates(userId)
                if (response.isSuccessful) {
                    val certificates = response.body() ?: emptyList()
                    if (certificates.isNotEmpty()) {
                        _certificate.value = certificates.first()
                    }
                } else {
                    _errorMessage.value = "Error al cargar certificado: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("QuizViewModel", "Error loading certificates", e)
                _errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
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
