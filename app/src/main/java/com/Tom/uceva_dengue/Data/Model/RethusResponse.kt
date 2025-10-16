package com.Tom.uceva_dengue.Data.Model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta del backend que proviene del servicio RETHUS
 *
 * Ejemplo de respuesta cuando NO está registrado:
 * {
 *   "status": "success",
 *   "message": "El Ciudadano de la identificación consultada,No se encuentra inscrito en elRegistro Único Nacional del Talento Humano en Salud (ReTHUS).\nSiga las orientaciones aquí..."
 * }
 *
 * Ejemplo de respuesta cuando SÍ está registrado:
 * {
 *   "status": "success",
 *   "message": ""
 * }
 */
data class RethusResponse(
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("message")
    val message: String? = null
) {
    /**
     * Determina si el profesional está registrado en RETHUS
     *
     * Lógica:
     * - Si message está vacío o null = SÍ está registrado (Rol 3 - Personal Médico)
     * - Si message contiene "no se encuentra inscrito" = NO está registrado (Rol 1 - Usuario)
     *
     * @return true si está validado en RETHUS, false si no
     */
    fun isSuccess(): Boolean {
        val messageText = message ?: ""

        // Si el mensaje está vacío, significa que SÍ está registrado en RETHUS
        if (messageText.isBlank()) {
            return true
        }

        // Si contiene el texto de "no se encuentra inscrito", NO está registrado
        val notRegistered = messageText.contains("No se encuentra inscrito", ignoreCase = true) ||
                           messageText.contains("no está inscrito", ignoreCase = true)

        // Retornar true solo si NO contiene el mensaje de "no inscrito"
        return !notRegistered
    }
}
