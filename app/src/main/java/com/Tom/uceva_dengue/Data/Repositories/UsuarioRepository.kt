package com.Tom.uceva_dengue.Data.Repositories

import com.Tom.uceva_dengue.Data.Database.MySQLDatabase
import com.Tom.uceva_dengue.Domain.Entities.Usuario
import com.Tom.uceva_dengue.Domain.Interface.IUsuarioRepository

class UsuarioRepository : IUsuarioRepository {
    override suspend fun crearUsuario(usuario: Usuario): Boolean {
        val connection = MySQLDatabase.getConnection()
        return try {
            connection?.let {
                val call = it.prepareCall("{CALL InsertarUsuario(?, ?, ?, ?, ?, ?, ?,?)}")

                call.setString(1, usuario.NOMBRE_USUARIO)
                call.setString(2, usuario.CORREO_USUARIO)
                call.setString(3, usuario.CONTRASENIA_USUARIO)
                call.setString(4, usuario.DIRECCION_USUARIO)
                call.setInt(5, usuario.FK_ID_ROL)
                call.setInt(6, usuario.FK_ID_MUNICIPIO)
                call.setInt(7, usuario.FK_ID_TIPOSANGRE)
                call.setInt(8, usuario.FK_ID_GENERO)

                val rowsAffected = call.executeUpdate()
                call.close()

                rowsAffected > 0
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            connection?.close()
        }
    }

    override suspend fun IniciarSesion(correo: String, contra: String): Usuario? {
        val connection = MySQLDatabase.getConnection()
        var usuario: Usuario? = null

        try {
            connection?.let {
                val call = it.prepareCall("{CALL ValidarUsuario(?, ?)}")
                call.setString(1, correo)
                call.setString(2, contra)

                val resultSet = call.executeQuery()

                if (resultSet.next()) {
                    usuario = Usuario(
                        ID_USUARIO = resultSet.getInt("ID_USUARIO"),
                        NOMBRE_USUARIO = resultSet.getString("NOMBRE_USUARIO"),
                        CORREO_USUARIO = resultSet.getString("CORREO_USUARIO"),
                        CONTRASENIA_USUARIO = resultSet.getString("CONTRASENIA_USUARIO"),
                        DIRECCION_USUARIO = resultSet.getString("DIRECCION_USUARIO"),
                        FK_ID_ROL = resultSet.getInt("FK_ID_ROL"),
                        NOMBRE_ROL = resultSet.getString("NOMBRE_ROL"),
                        FK_ID_MUNICIPIO = resultSet.getInt("FK_ID_MUNICIPIO"),
                        NOMBRE_MUNICIPIO = resultSet.getString("NOMBRE_MUNICIPIO"),
                        FK_ID_TIPOSANGRE = resultSet.getInt("FK_ID_TIPOSANGRE"),
                        NOMBRE_TIPOSANGRE = resultSet.getString("NOMBRE_TIPOSANGRE"),
                        FK_ID_GENERO = resultSet.getInt("FK_ID_GENERO"),
                        NOMBRE_GENERO = resultSet.getString("NOMBRE_GENERO")
                    )
                }

                resultSet.close()
                call.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.close()
        }

        return usuario
    }


}