package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Shared models used across different API responses for nested objects
 */

data class UserInfo(
    @SerializedName("ID_USUARIO")
    val ID_USUARIO: Int,

    @SerializedName("NOMBRE_USUARIO")
    val NOMBRE_USUARIO: String,

    @SerializedName("CORREO_USUARIO")
    val CORREO_USUARIO: String? = null,

    @SerializedName("NOMBRE_ROL")
    val NOMBRE_ROL: String? = null,

    @SerializedName("FECHA_NACIMIENTO_USUARIO")
    val FECHA_NACIMIENTO_USUARIO: String? = null
) {
    /**
     * Calcula la edad del paciente basándose en la fecha de nacimiento
     */
    fun calculateAge(): Int? {
        return try {
            FECHA_NACIMIENTO_USUARIO?.let { dateString ->
                val possibleFormats = listOf(
                    "yyyy-MM-dd",
                    "dd/MM/yyyy",
                    "yyyy/MM/dd"
                )

                var birthDate: java.time.LocalDate? = null
                for (format in possibleFormats) {
                    try {
                        val formatter = java.time.format.DateTimeFormatter.ofPattern(format)
                        birthDate = java.time.LocalDate.parse(dateString, formatter)
                        break
                    } catch (e: Exception) {
                        // Continuar con el siguiente formato
                    }
                }

                birthDate?.let {
                    java.time.Period.between(it, java.time.LocalDate.now()).years
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene el grupo etario según la clasificación de la OMS (1-5)
     */
    fun getAgeGroup(): Int? {
        val age = calculateAge() ?: return null
        return when {
            age <= 4 -> 1      // 0-4 años
            age <= 14 -> 2     // 5-14 años
            age <= 49 -> 3     // 15-49 años
            age <= 64 -> 4     // 50-64 años
            else -> 5          // 65+ años
        }
    }
}

data class CityInfo(
    @SerializedName("ID_MUNICIPIO")
    val ID_MUNICIPIO: Int,

    @SerializedName("NOMBRE_MUNICIPIO")
    val NOMBRE_MUNICIPIO: String,

    @SerializedName("FK_ID_DEPARTAMENTO")
    val FK_ID_DEPARTAMENTO: Int,

    @SerializedName("DEPARTAMENTO")
    val DEPARTAMENTO: DepartmentInfo? = null
)

data class DepartmentInfo(
    @SerializedName("ID_DEPARTAMENTO")
    val ID_DEPARTAMENTO: Int,

    @SerializedName("NOMBRE_DEPARTAMENTO")
    val NOMBRE_DEPARTAMENTO: String
)
