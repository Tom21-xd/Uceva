package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Estado del paciente
 * Catálogo de estados clínicos: Ambulatorio, Observación, Hospitalizado, UCI, Recuperado, Fallecido
 */
data class PatientStateModel(
    @SerializedName("ID_ESTADO_PACIENTE")
    val ID_ESTADO_PACIENTE: Int,

    @SerializedName("NOMBRE_ESTADO_PACIENTE")
    val NOMBRE_ESTADO_PACIENTE: String,

    @SerializedName("NIVEL_GRAVEDAD")
    val NIVEL_GRAVEDAD: Int, // 1=Leve, 2=Moderado, 3=Grave, 4=Crítico, 5=Resuelto

    @SerializedName("DESCRIPCION")
    val DESCRIPCION_ESTADO: String? = null,

    @SerializedName("COLOR_INDICADOR")
    val COLOR_INDICADOR: String? = null,

    @SerializedName("ESTADO_ACTIVO")
    val ESTADO_ACTIVO: Boolean = true
)

/**
 * Evolución clínica de un caso
 * Registra el seguimiento día a día del paciente con signos vitales, laboratorios y observaciones
 */
data class CaseEvolutionModel(
    @SerializedName("ID_EVOLUCION")
    val ID_EVOLUCION: Int,

    @SerializedName("FK_ID_CASO")
    val FK_ID_CASO: Int,

    @SerializedName("FK_ID_MEDICO")
    val FK_ID_MEDICO: Int,

    @SerializedName("FK_ID_TIPODENGUE")
    val FK_ID_TIPO_DENGUE: Int,

    @SerializedName("FK_ID_ESTADO_PACIENTE")
    val FK_ID_ESTADO_PACIENTE: Int,

    @SerializedName("FECHA_EVOLUCION")
    val FECHA_EVOLUCION: String,

    @SerializedName("DIA_ENFERMEDAD")
    val DIA_ENFERMEDAD: Int? = null,

    @SerializedName("SINTOMAS_REPORTADOS")
    val SINTOMAS_REPORTADOS: String = "[]", // JSON con IDs de síntomas

    // ===== SIGNOS VITALES =====

    @SerializedName("TEMPERATURA")
    val TEMPERATURA: Double? = null,

    @SerializedName("PRESION_ARTERIAL_SISTOLICA")
    val PRESION_ARTERIAL_SISTOLICA: Int? = null,

    @SerializedName("PRESION_ARTERIAL_DIASTOLICA")
    val PRESION_ARTERIAL_DIASTOLICA: Int? = null,

    @SerializedName("FRECUENCIA_CARDIACA")
    val FRECUENCIA_CARDIACA: Int? = null,

    @SerializedName("FRECUENCIA_RESPIRATORIA")
    val FRECUENCIA_RESPIRATORIA: Int? = null,

    @SerializedName("SATURACION_OXIGENO")
    val SATURACION_OXIGENO: Double? = null,

    // ===== LABORATORIOS =====

    @SerializedName("PLAQUETAS")
    val PLAQUETAS: Int? = null,

    @SerializedName("HEMATOCRITO")
    val HEMATOCRITO: Double? = null,

    @SerializedName("HEMOGLOBINA")
    val HEMOGLOBINA: Double? = null,

    @SerializedName("LEUCOCITOS")
    val LEUCOCITOS: Int? = null,

    @SerializedName("TRANSAMINASAS_AST")
    val AST: Int? = null,

    @SerializedName("TRANSAMINASAS_ALT")
    val ALT: Int? = null,

    // ===== EVALUACIÓN CLÍNICA =====

    @SerializedName("OBSERVACIONES_CLINICAS")
    val OBSERVACIONES_CLINICAS: String? = null,

    @SerializedName("TRATAMIENTO_INDICADO")
    val TRATAMIENTO_INDICADO: String? = null,

    @SerializedName("EXAMENES_SOLICITADOS")
    val EXAMENES_SOLICITADOS: String? = null,

    // ===== ALERTAS Y CAMBIOS =====

    @SerializedName("CAMBIO_TIPO_DENGUE")
    val CAMBIO_TIPO_DENGUE: Boolean = false,

    @SerializedName("EMPEORAMIENTO_DETECTADO")
    val EMPEORAMIENTO_DETECTADO: Boolean = false,

    @SerializedName("REQUIERE_HOSPITALIZACION")
    val REQUIERE_HOSPITALIZACION: Boolean = false,

    @SerializedName("REQUIERE_UCI")
    val REQUIERE_UCI: Boolean = false,

    // ===== SEGUIMIENTO =====

    @SerializedName("PROXIMA_CITA")
    val PROXIMA_CITA: String? = null,

    @SerializedName("RECOMENDACIONES_PACIENTE")
    val RECOMENDACIONES_PACIENTE: String? = null,

    // ===== METADATA =====

    @SerializedName("ESTADO_EVOLUCION")
    val ESTADO_EVOLUCION: Boolean = true,

    @SerializedName("FECHA_REGISTRO")
    val FECHA_REGISTRO: String? = null,

    @SerializedName("FECHA_MODIFICACION")
    val FECHA_MODIFICACION: String? = null,

    // ===== NESTED OBJECTS (from Includes) =====

    @SerializedName("patientState")
    val ESTADO_PACIENTE: PatientStateModel? = null,

    @SerializedName("doctor")
    val MEDICO: UserInfo? = null,

    @SerializedName("typeOfDengue")
    val TIPO_DENGUE: TypeOfDengueInfo? = null
)

/**
 * Request para crear evolución clínica
 * IMPORTANTE: El backend recibe CaseEvolution entity directamente
 */
data class CreateCaseEvolutionRequest(
    @SerializedName("FK_ID_ESTADO_PACIENTE")
    val FK_ID_ESTADO_PACIENTE: Int,

    @SerializedName("FK_ID_TIPODENGUE")
    val FK_ID_TIPODENGUE: Int,

    @SerializedName("FK_ID_MEDICO")
    val FK_ID_MEDICO: Int,

    @SerializedName("DIA_ENFERMEDAD")
    val DIA_ENFERMEDAD: Int? = null,

    @SerializedName("SINTOMAS_REPORTADOS")
    val SINTOMAS_REPORTADOS: String = "[]",

    // Signos vitales
    @SerializedName("TEMPERATURA")
    val TEMPERATURA: Double? = null,

    @SerializedName("PRESION_ARTERIAL_SISTOLICA")
    val PRESION_ARTERIAL_SISTOLICA: Int? = null,

    @SerializedName("PRESION_ARTERIAL_DIASTOLICA")
    val PRESION_ARTERIAL_DIASTOLICA: Int? = null,

    @SerializedName("FRECUENCIA_CARDIACA")
    val FRECUENCIA_CARDIACA: Int? = null,

    @SerializedName("FRECUENCIA_RESPIRATORIA")
    val FRECUENCIA_RESPIRATORIA: Int? = null,

    @SerializedName("SATURACION_OXIGENO")
    val SATURACION_OXIGENO: Double? = null,

    // Laboratorios
    @SerializedName("PLAQUETAS")
    val PLAQUETAS: Int? = null,

    @SerializedName("HEMATOCRITO")
    val HEMATOCRITO: Double? = null,

    @SerializedName("HEMOGLOBINA")
    val HEMOGLOBINA: Double? = null,

    @SerializedName("LEUCOCITOS")
    val LEUCOCITOS: Int? = null,

    @SerializedName("TRANSAMINASAS_AST")
    val TRANSAMINASAS_AST: Int? = null,

    @SerializedName("TRANSAMINASAS_ALT")
    val TRANSAMINASAS_ALT: Int? = null,

    // Observaciones y tratamiento
    @SerializedName("OBSERVACIONES_CLINICAS")
    val OBSERVACIONES_CLINICAS: String? = null,

    @SerializedName("TRATAMIENTO_INDICADO")
    val TRATAMIENTO_INDICADO: String? = null,

    @SerializedName("EXAMENES_SOLICITADOS")
    val EXAMENES_SOLICITADOS: String? = null,

    // Alertas
    @SerializedName("REQUIERE_HOSPITALIZACION")
    val REQUIERE_HOSPITALIZACION: Boolean = false,

    @SerializedName("REQUIERE_UCI")
    val REQUIERE_UCI: Boolean = false,

    @SerializedName("PROXIMA_CITA")
    val PROXIMA_CITA: String? = null,

    @SerializedName("RECOMENDACIONES_PACIENTE")
    val RECOMENDACIONES_PACIENTE: String? = null
)

/**
 * Resumen de evolución de un caso
 * Para mostrar gráficas y tendencias
 */
data class CaseEvolutionSummaryModel(
    @SerializedName("ID_CASO")
    val ID_CASO: Int,

    @SerializedName("ESTADO_ACTUAL")
    val ESTADO_ACTUAL: PatientStateModel,

    @SerializedName("DIA_ENFERMEDAD_ACTUAL")
    val DIA_ENFERMEDAD_ACTUAL: Int,

    @SerializedName("TOTAL_EVOLUCIONES")
    val TOTAL_EVOLUCIONES: Int,

    @SerializedName("ULTIMA_EVOLUCION")
    val ULTIMA_EVOLUCION: CaseEvolutionModel?,

    @SerializedName("TENDENCIA_PLAQUETAS")
    val TENDENCIA_PLAQUETAS: String? = null, // "mejorando", "estable", "empeorando"

    @SerializedName("TENDENCIA_HEMATOCRITO")
    val TENDENCIA_HEMATOCRITO: String? = null,

    @SerializedName("TIENE_SIGNOS_ALARMA")
    val TIENE_SIGNOS_ALARMA: Boolean = false,

    @SerializedName("EVOLUCIONES")
    val EVOLUCIONES: List<CaseEvolutionModel>? = null
)
