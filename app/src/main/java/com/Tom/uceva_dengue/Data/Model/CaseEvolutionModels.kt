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

    @SerializedName("DESCRIPCION_ESTADO")
    val DESCRIPCION_ESTADO: String? = null
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

    @SerializedName("FK_ID_ESTADO_PACIENTE")
    val FK_ID_ESTADO_PACIENTE: Int,

    @SerializedName("DIA_ENFERMEDAD")
    val DIA_ENFERMEDAD: Int,

    @SerializedName("FECHA_EVOLUCION")
    val FECHA_EVOLUCION: String,

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
    val SATURACION_OXIGENO: Int? = null,

    // ===== LABORATORIOS =====

    @SerializedName("PLAQUETAS")
    val PLAQUETAS: Int? = null,

    @SerializedName("HEMATOCRITO")
    val HEMATOCRITO: Double? = null,

    @SerializedName("HEMOGLOBINA")
    val HEMOGLOBINA: Double? = null,

    @SerializedName("LEUCOCITOS")
    val LEUCOCITOS: Int? = null,

    @SerializedName("CREATININA")
    val CREATININA: Double? = null,

    @SerializedName("TRANSAMINASAS")
    val TRANSAMINASAS: Int? = null,

    // ===== SÍNTOMAS Y OBSERVACIONES =====

    @SerializedName("SINTOMAS_REPORTADOS")
    val SINTOMAS_REPORTADOS: String? = null, // JSON con IDs de síntomas

    @SerializedName("DOLOR_ABDOMINAL")
    val DOLOR_ABDOMINAL: Boolean = false,

    @SerializedName("VOMITO_PERSISTENTE")
    val VOMITO_PERSISTENTE: Boolean = false,

    @SerializedName("SANGRADO_MUCOSAS")
    val SANGRADO_MUCOSAS: Boolean = false,

    @SerializedName("LETARGIA")
    val LETARGIA: Boolean = false,

    @SerializedName("HEPATOMEGALIA")
    val HEPATOMEGALIA: Boolean = false,

    @SerializedName("ACUMULACION_LIQUIDOS")
    val ACUMULACION_LIQUIDOS: Boolean = false,

    @SerializedName("OBSERVACIONES_CLINICAS")
    val OBSERVACIONES_CLINICAS: String? = null,

    // ===== ALERTAS =====

    @SerializedName("EMPEORAMIENTO_DETECTADO")
    val EMPEORAMIENTO_DETECTADO: Boolean = false,

    @SerializedName("REQUIERE_ATENCION_INMEDIATA")
    val REQUIERE_ATENCION_INMEDIATA: Boolean = false,

    @SerializedName("SIGNOS_ALARMA")
    val SIGNOS_ALARMA: String? = null, // JSON con signos de alarma detectados

    // ===== TRATAMIENTO =====

    @SerializedName("TRATAMIENTO_ADMINISTRADO")
    val TRATAMIENTO_ADMINISTRADO: String? = null,

    @SerializedName("HIDRATACION_ORAL")
    val HIDRATACION_ORAL: Boolean = false,

    @SerializedName("HIDRATACION_INTRAVENOSA")
    val HIDRATACION_INTRAVENOSA: Boolean = false,

    @SerializedName("VOLUMEN_LIQUIDOS_ML")
    val VOLUMEN_LIQUIDOS_ML: Int? = null,

    // ===== METADATA =====

    @SerializedName("FK_ID_PROFESIONAL_SALUD")
    val FK_ID_PROFESIONAL_SALUD: Int? = null,

    @SerializedName("ESTADO_EVOLUCION")
    val ESTADO_EVOLUCION: Boolean = true,

    // ===== NESTED OBJECTS =====

    @SerializedName("ESTADO_PACIENTE")
    val ESTADO_PACIENTE: PatientStateModel? = null,

    @SerializedName("PROFESIONAL")
    val PROFESIONAL: UserInfo? = null
)

/**
 * Request para crear evolución clínica
 */
data class CreateCaseEvolutionRequest(
    @SerializedName("FK_ID_CASO")
    val FK_ID_CASO: Int,

    @SerializedName("FK_ID_ESTADO_PACIENTE")
    val FK_ID_ESTADO_PACIENTE: Int,

    @SerializedName("DIA_ENFERMEDAD")
    val DIA_ENFERMEDAD: Int,

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
    val SATURACION_OXIGENO: Int? = null,

    // Laboratorios
    @SerializedName("PLAQUETAS")
    val PLAQUETAS: Int? = null,

    @SerializedName("HEMATOCRITO")
    val HEMATOCRITO: Double? = null,

    @SerializedName("HEMOGLOBINA")
    val HEMOGLOBINA: Double? = null,

    @SerializedName("LEUCOCITOS")
    val LEUCOCITOS: Int? = null,

    // Síntomas
    @SerializedName("SINTOMAS_REPORTADOS")
    val SINTOMAS_REPORTADOS: List<Int>? = null, // IDs de síntomas

    @SerializedName("DOLOR_ABDOMINAL")
    val DOLOR_ABDOMINAL: Boolean = false,

    @SerializedName("VOMITO_PERSISTENTE")
    val VOMITO_PERSISTENTE: Boolean = false,

    @SerializedName("SANGRADO_MUCOSAS")
    val SANGRADO_MUCOSAS: Boolean = false,

    @SerializedName("LETARGIA")
    val LETARGIA: Boolean = false,

    @SerializedName("OBSERVACIONES_CLINICAS")
    val OBSERVACIONES_CLINICAS: String? = null,

    // Tratamiento
    @SerializedName("TRATAMIENTO_ADMINISTRADO")
    val TRATAMIENTO_ADMINISTRADO: String? = null,

    @SerializedName("HIDRATACION_ORAL")
    val HIDRATACION_ORAL: Boolean = false,

    @SerializedName("HIDRATACION_INTRAVENOSA")
    val HIDRATACION_INTRAVENOSA: Boolean = false,

    @SerializedName("VOLUMEN_LIQUIDOS_ML")
    val VOLUMEN_LIQUIDOS_ML: Int? = null
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
