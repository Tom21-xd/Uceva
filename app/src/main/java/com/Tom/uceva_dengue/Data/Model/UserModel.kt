package com.Tom.uceva_dengue.Data.Model

import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

data class UserModel(
    val ID_USUARIO: Int = 0,
    val NOMBRE_USUARIO: String? = null,
    val CORREO_USUARIO: String? = null,
    val CONTRASENIA_USUARIO: String? = null,
    val DIRECCION_USUARIO: String? = null,
    val FECHA_NACIMIENTO_USUARIO: String? = null,
    val FK_ID_ROL: Int = 0,
    val NOMBRE_ROL: String? = null,
    val FK_ID_MUNICIPIO: Int? = null,
    val NOMBRE_MUNICIPIO: String? = null,
    val FK_ID_TIPOSANGRE: Int = 0,
    val NOMBRE_TIPOSANGRE: String? = null,
    val FK_ID_GENERO: Int = 0,
    val NOMBRE_GENERO: String? = null,
    val ID_DEPARTAMENTO: Int = 0,
    val NOMBRE_ESTADOUSUARIO: String? = null
) {
    /**
     * Calcula la edad del usuario basándose en la fecha de nacimiento
     * @return Edad en años, o null si no hay fecha de nacimiento
     */
    fun calculateAge(): Int? {
        return try {
            FECHA_NACIMIENTO_USUARIO?.let { dateString ->
                // Intentar varios formatos de fecha
                val possibleFormats = listOf(
                    "yyyy-MM-dd",
                    "dd/MM/yyyy",
                    "yyyy/MM/dd"
                )

                var birthDate: LocalDate? = null
                for (format in possibleFormats) {
                    try {
                        val formatter = DateTimeFormatter.ofPattern(format)
                        birthDate = LocalDate.parse(dateString, formatter)
                        break
                    } catch (e: Exception) {
                        // Continuar con el siguiente formato
                    }
                }

                birthDate?.let {
                    Period.between(it, LocalDate.now()).years
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene el grupo etario según la clasificación de la OMS
     * @return Código del grupo etario (1-5), o null si no se puede calcular
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

    /**
     * Obtiene el nombre del grupo etario según la OMS
     * @return Nombre del grupo etario
     */
    fun getAgeGroupName(): String {
        val age = calculateAge() ?: return "Edad desconocida"
        return when {
            age <= 4 -> "0-4 años"
            age <= 14 -> "5-14 años"
            age <= 49 -> "15-49 años"
            age <= 64 -> "50-64 años"
            else -> "65+ años"
        }
    }
}

