package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.CasoReportado
import com.Tom.uceva_dengue.Domain.Interface.ICasosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.CallableStatement

class CasoReportadoRepository : ICasosRepository {
    override suspend fun getCasos(): Flow<List<CasoReportado>> = flow {
        val connection = MySQLDatabase.getConnection()
        val casos = mutableListOf<CasoReportado>()

        try {
            connection?.let {
                val call: CallableStatement = it.prepareCall("{CALL ListarCaso()}")
                val resultSet = call.executeQuery()

                while (resultSet.next()) {
                    val caso = CasoReportado(
                        ID_CASOREPORTADO = resultSet.getInt("ID_CASOREPORTADO"),
                        DESCRIPCION_CASOREPORTADO = resultSet.getString("DESCRIPCION_CASOREPORTADO"),
                        FECHA_CASOREPORTADO = resultSet.getString("FECHA_CASOREPORTADO"),
                        FK_ID_ESTADOCASO = resultSet.getInt("FK_ID_ESTADOCASO"),
                        NOMBRE_ESTADOCASO = resultSet.getString("NOMBRE_ESTADOCASO"),
                        ID_DEPARTAMENTO = resultSet.getInt("ID_DEPARTAMENTO"),
                        ID_MUNICIPIO = resultSet.getInt("ID_MUNICIPIO"),
                        FK_ID_HOSPITAL = resultSet.getInt("FK_ID_HOSPITAL"),
                        FK_ID_TIPODENGUE = resultSet.getInt("FK_ID_TIPODENGUE"),
                        FK_ID_PACIENTE = resultSet.getInt("FK_ID_PACIENTE"),
                        NOMBRE_PACIENTE = resultSet.getString("NOMBRE_PACIENTE"),
                        FK_ID_PERSONALMEDICO = resultSet.getInt("FK_ID_PERSONALMEDICO"),
                        NOMBRE_PERSONALMEDICO = resultSet.getString("NOMBRE_PERSONALMEDICO"),
                        FECHAFINALIZACION_CASO = resultSet.getString("FECHAFINALIZACION_CASO"),
                        DIRECCION_CASOREPORTADO = resultSet.getString("DIRECCION_CASOREPORTADO")
                    )
                    casos.add(caso)
                }

                resultSet.close()
                call.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        emit(casos)
    }
}
