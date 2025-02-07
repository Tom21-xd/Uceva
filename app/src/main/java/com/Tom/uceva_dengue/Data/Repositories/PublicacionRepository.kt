package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.Publicacion
import com.Tom.uceva_dengue.Domain.Interface.IPublicacionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.CallableStatement

class PublicacionRepository : IPublicacionRepository {


    override fun getPublicaciones(): Flow<List<Publicacion>> = flow {
        val connection = MySQLDatabase.getConnection()
        val publicaciones = mutableListOf<Publicacion>()

        try {
            connection?.let {
                val call: CallableStatement = it.prepareCall("{CALL ListarPublicaciones()}")
                val resultSet = call.executeQuery()

                while (resultSet.next()) {
                    val publicacion = Publicacion(
                        Id = resultSet.getString("ID_PUBLICACION"),
                        Titulo = resultSet.getString("TITULO"),
                        Descripcion = resultSet.getString("DESCRIPCION"),
                        FechaPublicacion = resultSet.getString("FECHA_PUBLICACION"),
                        Usuario = resultSet.getString("ID_USUARIO")
                    )
                    publicaciones.add(publicacion)
                }

                resultSet.close()
                call.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        emit(publicaciones)
    }


}
