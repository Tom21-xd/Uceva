package com.Tom.uceva_dengue.Domain.Entities

data class CasoReportado(
    var ID_CASOREPORTADO: Int = 0,
    var DESCRIPCION_CASOREPORTADO: String? = null,
    var FECHA_CASOREPORTADO: String? = null,
    var FK_ID_ESTADOCASO: Int = 0,
    var NOMBRE_ESTADOCASO: String? = null,
    var ID_DEPARTAMENTO: Int = 0,
    var ID_MUNICIPIO: Int = 0,
    var FK_ID_HOSPITAL: Int = 0,
    var FK_ID_TIPODENGUE: Int = 0,
    var FK_ID_PACIENTE: Int = 0,
    var NOMBRE_PACIENTE: String? = null,
    var FK_ID_PERSONALMEDICO: Int = 0,
    var NOMBRE_PERSONALMEDICO: String? = null,
    var FECHAFINALIZACION_CASO: String? = null,
    var DIRECCION_CASOREPORTADO: String? = null
)
