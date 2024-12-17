package com.Tom.uceva_dengue.model

import java.util.Date

data class CasoReportado(
    var id: String = "",
    var fecha: Date? = null,
    var tipoDengue: String? = null,
    var usuarioRef: String = "",
    var personalMedicoRef: String = ""
)
