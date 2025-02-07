package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.Municipio
import com.Tom.uceva_dengue.Domain.Interface.IMunicipioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import java.sql.CallableStatement

class MunicipioRepository : IMunicipioRepository {
    override fun getMunicipios(departamentoId: String): Flow<List<Municipio>> = flow {
        val connection = MySQLDatabase.getConnection()
        val municipios = mutableListOf<Municipio>()

        try {
            connection?.let {
                val call: CallableStatement = it.prepareCall("{CALL ListarMunicipio(?)}")
                call.setString(1, departamentoId) // Pasamos el departamento como parámetro

                val resultSet = call.executeQuery()

                while (resultSet.next()) {
                    val municipio = Municipio(
                        Id = resultSet.getString("ID_MUNICIPIO"),
                        Nombre = resultSet.getString("NOMBRE_MUNICIPIO"),
                        Fk_id_departamento = resultSet.getString("FK_ID_DEPARTAMENTO")
                    )
                    municipios.add(municipio)
                }

                resultSet.close()
                call.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        emit(municipios)
    }
}