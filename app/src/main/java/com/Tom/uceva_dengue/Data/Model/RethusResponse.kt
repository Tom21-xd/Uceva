package com.Tom.uceva_dengue.Data.Model

data class RethusResponse(
    val message: String,
    val data: String
) {
    fun isSuccess(): Boolean {
        return !data.contains("El Ciudadano de la identificación consultada,No se encuentra inscrito en elRegistro Único Nacional del Talento Humano en Salud (ReTHUS).")
    }
}
