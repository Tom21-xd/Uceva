package com.Tom.uceva_dengue.Data.Repositories

import IDepartamentoRepository
import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.Departamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.CallableStatement

class DepartmentRepository : IDepartamentoRepository {
    override fun getDepartamentos(): Flow<List<Departamento>> = flow {
        val connection = MySQLDatabase.getConnection()
        val departamentos = mutableListOf<Departamento>()

        connection?.let {
            val call: CallableStatement = it.prepareCall("{CALL ListarDepartamento()}")
            val resultSet = call.executeQuery()

            while (resultSet.next()) {
                val departamento = Departamento(
                    Id = resultSet.getString("ID_DEPARTAMENTO"),
                    Nombre = resultSet.getString("NOMBRE_DEPARTAMENTO"),
                )
                departamentos.add(departamento)
            }

            resultSet.close()
            call.close()
            it.close()
        }

        emit(departamentos)
    }
}
