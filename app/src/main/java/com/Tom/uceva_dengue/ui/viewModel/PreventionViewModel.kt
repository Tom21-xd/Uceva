package com.Tom.uceva_dengue.ui.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Tom.uceva_dengue.Data.Api.RetrofitClient
import com.Tom.uceva_dengue.Data.Model.PreventionCategory
import com.Tom.uceva_dengue.Data.Model.PreventionImage
import com.Tom.uceva_dengue.Data.Model.PreventionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PreventionViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<PreventionCategory>>(emptyList())
    val categories: StateFlow<List<PreventionCategory>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _hasLoadedFromApi = MutableStateFlow(false)
    val hasLoadedFromApi: StateFlow<Boolean> = _hasLoadedFromApi.asStateFlow()

    init {
        loadPreventionContent()
    }

    fun loadPreventionContent() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("PreventionViewModel", "Loading prevention content from API...")

                val response = RetrofitClient.preventionService.getPreventionCategories()

                if (response.isSuccessful && response.body() != null) {
                    val apiCategories = response.body()!!
                    Log.d("PreventionViewModel", "Loaded ${apiCategories.size} categories from API")

                    if (apiCategories.isNotEmpty()) {
                        _categories.value = apiCategories
                        _hasLoadedFromApi.value = true
                    } else {
                        Log.d("PreventionViewModel", "API returned empty, using fallback")
                        _categories.value = getFallbackCategories()
                    }
                } else {
                    Log.e("PreventionViewModel", "API error: ${response.code()} - ${response.message()}")
                    _error.value = "Error al cargar el contenido"
                    _categories.value = getFallbackCategories()
                }
            } catch (e: Exception) {
                Log.e("PreventionViewModel", "Exception loading content: ${e.message}", e)
                _error.value = e.message
                _categories.value = getFallbackCategories()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refresh() {
        loadPreventionContent()
    }

    /**
     * Fallback content - used when API is unavailable
     * This is the original hardcoded content from PreventionGuideScreen
     */
    private fun getFallbackCategories(): List<PreventionCategory> {
        return listOf(
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 1,
                NOMBRE_CATEGORIA = "¿Qué es el Dengue?",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "ℹ️",  // Emoji directo
                COLOR = "info",
                ORDEN_VISUALIZACION = 1,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(1, 1, "Vector: Mosquito Aedes aegypti",
                        "Enfermedad viral transmitida por la picadura del mosquito Aedes aegypti. Se caracteriza por rayas blancas en patas y cuerpo, y pica durante el día, especialmente en las primeras horas de la mañana y al atardecer.",
                        "🦟", false, 1, true),
                    PreventionItem(2, 1, "Agente: Virus del dengue",
                        "El virus se transmite cuando el mosquito adquiere el virus al picar a una persona infectada, y posteriormente puede transmitirlo a otras personas sanas.",
                        "🦠", false, 2, true),
                    PreventionItem(3, 1, "Huésped: Ser humano",
                        "Este ciclo de transmisión convierte al mosquito en el vector principal de la enfermedad, siendo fundamental comprender su comportamiento para implementar medidas preventivas efectivas.",
                        "👤", false, 3, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 2,
                NOMBRE_CATEGORIA = "Síntomas Iniciales del Dengue",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "🌡️",  // Emoji directo
                COLOR = "warning",
                ORDEN_VISUALIZACION = 2,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(4, 2, "Período de incubación",
                        "Los síntomas iniciales del dengue suelen aparecer entre 4 y 10 días después de la picadura del mosquito infectado. Es fundamental reconocerlos tempranamente para buscar atención médica oportuna y evitar complicaciones.",
                        "⏱️", false, 1, true),
                    PreventionItem(5, 2, "Fiebre Alta",
                        "Temperatura corporal elevada de inicio súbito, generalmente superior a 38.5°C, que puede durar entre 2 y 7 días.",
                        "🌡️", false, 2, true),
                    PreventionItem(6, 2, "Cefalea Intensa",
                        "Dolor de cabeza severo y persistente, frecuentemente acompañado de dolor retroocular que se intensifica con el movimiento de los ojos.",
                        "🧠", false, 3, true),
                    PreventionItem(7, 2, "Dolor Muscular",
                        "Malestar general con dolores musculares y articulares intensos, razón por la cual el dengue también se conoce como 'fiebre rompehuesos'.",
                        "💪", false, 4, true),
                    PreventionItem(8, 2, "Erupciones Cutáneas",
                        "Aparición de manchas rojizas en la piel que pueden presentarse en diferentes momentos de la enfermedad.",
                        "🔴", false, 5, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 3,
                NOMBRE_CATEGORIA = "Signos de Alarma",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "🚨",  // Emoji directo
                COLOR = "danger",
                ORDEN_VISUALIZACION = 3,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(9, 3, "Atención Inmediata Requerida",
                        "Los signos de alarma del dengue aparecen típicamente entre el tercer y quinto día de la enfermedad, coincidiendo con la fase crítica. Ante la presencia de cualquiera de estos signos, se debe acudir inmediatamente al servicio de salud.",
                        "⚠️", true, 1, true),
                    PreventionItem(10, 3, "Dolor Abdominal Intenso",
                        "Dolor abdominal intenso y continuo, o dolor a la palpación del abdomen, que puede indicar complicaciones graves.",
                        "🤢", false, 2, true),
                    PreventionItem(11, 3, "Vómitos Persistentes",
                        "Vómitos frecuentes que impiden la hidratación oral adecuada y pueden llevar a deshidratación severa.",
                        "🤮", false, 3, true),
                    PreventionItem(12, 3, "Sangrado de Mucosas",
                        "Sangrado de encías, nariz, o presencia de sangre en vómito u orina, señal de alteraciones en la coagulación.",
                        "🩸", false, 4, true),
                    PreventionItem(13, 3, "Alteración del Estado Mental",
                        "Somnolencia excesiva, irritabilidad, confusión o inquietud que pueden indicar compromiso neurológico.",
                        "😴", false, 5, true),
                    PreventionItem(14, 3, "Acumulación de Líquidos",
                        "Dificultad respiratoria por acumulación de líquidos en el pecho o abdomen distendido.",
                        "💧", false, 6, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 4,
                NOMBRE_CATEGORIA = "Prevención: Eliminación de Criaderos",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "💧",  // Emoji directo
                COLOR = "primary",
                ORDEN_VISUALIZACION = 4,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(15, 4, "Estrategia Principal",
                        "La prevención del dengue se basa fundamentalmente en eliminar los criaderos de mosquitos y evitar las picaduras.",
                        "💡", false, 1, true),
                    PreventionItem(16, 4, "Tapar Depósitos",
                        "Mantener tapados todos los tanques y depósitos de agua para evitar que los mosquitos depositen sus huevos.",
                        "🚰", false, 2, true),
                    PreventionItem(17, 4, "Cambiar Agua",
                        "Cambiar el agua de los floreros cada dos días, lavando bien las paredes del recipiente con cepillo.",
                        "🔄", false, 3, true),
                    PreventionItem(18, 4, "Limpiar Espacios",
                        "Mantener patios, jardines y áreas comunes limpios, sin objetos que acumulen agua estancada.",
                        "🧹", false, 4, true),
                    PreventionItem(19, 4, "Eliminar Llantas",
                        "Eliminar, perforar o almacenar bajo techo las llantas y recipientes que puedan acumular agua de lluvia.",
                        "🚮", false, 5, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 5,
                NOMBRE_CATEGORIA = "Protección Personal contra Picaduras",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "🛡️",  // Emoji directo
                COLOR = "success",
                ORDEN_VISUALIZACION = 5,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(20, 5, "Horarios Críticos",
                        "Protegerse especialmente durante las horas de mayor actividad del Aedes aegypti: primeras horas de la mañana y al atardecer.",
                        "🕐", false, 1, true),
                    PreventionItem(21, 5, "Usar ropa de manga larga",
                        "Preferiblemente de colores claros.",
                        "👕", false, 2, true),
                    PreventionItem(22, 5, "Aplicar repelente",
                        "En la piel expuesta, siguiendo las instrucciones del producto.",
                        "🧴", false, 3, true),
                    PreventionItem(23, 5, "Instalar mosquiteros",
                        "En puertas y ventanas de la vivienda.",
                        "🪟", false, 4, true),
                    PreventionItem(24, 5, "Usar mosquiteros sobre las camas",
                        "Especialmente para proteger a bebés y niños pequeños.",
                        "❄️", false, 5, true),
                    PreventionItem(25, 5, "Usar ventiladores o aire acondicionado",
                        "Ya que el mosquito evita las corrientes de aire.",
                        "💨", false, 6, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 6,
                NOMBRE_CATEGORIA = "Mitos y Realidades sobre el Dengue",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "🧠",  // Emoji directo
                COLOR = "purple",
                ORDEN_VISUALIZACION = 6,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(26, 6, "Información correcta",
                        "Es fundamental conocer la información científicamente validada para tomar decisiones correctas en el cuidado de la salud.",
                        "ℹ️", false, 1, true),
                    PreventionItem(27, 6, "Mito: Transmisión Geográfica",
                        "El dengue solo se transmite en zonas rurales.\n\nRealidad: El dengue también ocurre en áreas urbanas.",
                        "🏙️", false, 2, true),
                    PreventionItem(28, 6, "Mito: Tratamiento con Antibióticos",
                        "El dengue se cura con antibióticos.\n\nRealidad: No existe tratamiento específico para el dengue. Los antibióticos no son efectivos contra virus.",
                        "💊", false, 3, true)
                )
            ),
            PreventionCategory(
                ID_CATEGORIA_PREVENCION = 7,
                NOMBRE_CATEGORIA = "Qué Hacer ante la Sospecha de Dengue",
                DESCRIPCION_CATEGORIA = null,
                ICONO = "⚕️",  // Emoji directo
                COLOR = "danger",
                ORDEN_VISUALIZACION = 7,
                ESTADO_CATEGORIA = true,
                FECHA_CREACION = null,
                IMAGENES = emptyList(),
                ITEMS = listOf(
                    PreventionItem(29, 7, "Importancia de la atención temprana",
                        "Si se presentan síntomas compatibles con dengue, es fundamental actuar de manera adecuada para evitar complicaciones.",
                        "⚕️", true, 1, true),
                    PreventionItem(30, 7, "01 - Descansar Adecuadamente",
                        "Guardar reposo en cama y evitar actividades físicas que puedan empeorar los síntomas.",
                        "🛌", false, 2, true),
                    PreventionItem(31, 7, "02 - Hidratarse Constantemente",
                        "Beber abundantes líquidos: agua, suero oral, jugos naturales y caldos.",
                        "💧", false, 3, true),
                    PreventionItem(32, 7, "03 - Evitar Automedicación",
                        "NO tomar aspirina ni ibuprofeno. Solo usar acetaminofén bajo supervisión médica.",
                        "❌", false, 4, true),
                    PreventionItem(33, 7, "04 - Acudir al Centro de Salud",
                        "Buscar atención médica inmediata para valoración, diagnóstico y seguimiento apropiado.",
                        "🏥", false, 5, true)
                )
            )
        )
    }
}
