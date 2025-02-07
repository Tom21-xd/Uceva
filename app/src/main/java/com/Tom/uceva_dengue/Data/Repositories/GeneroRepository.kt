package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.Genero
import com.Tom.uceva_dengue.Domain.Interface.IGeneroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.CallableStatement

class GeneroRepository : IGeneroRepository {

    override fun getGeneros(): Flow<List<Genero>> = flow {
        val connection = MySQLDatabase.getConnection()
        val generos = mutableListOf<Genero>()

        try {
            connection?.let {
                val call: CallableStatement = it.prepareCall("{CALL ListarGenero()}")
                val resultSet = call.executeQuery()

                while (resultSet.next()) {
                    val genero = Genero(
                        Id = resultSet.getString("ID_GENERO"),
                        Nombre = resultSet.getString("NOMBRE_GENERO"),
                        Estado = resultSet.getBoolean("ESTADO_GENERO")
                    )
                    generos.add(genero)
                }

                resultSet.close()
                call.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        emit(generos)
    }
}
