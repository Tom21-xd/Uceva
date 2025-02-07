package com.Tom.uceva_dengue.Data.Database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object MySQLDatabase {
    private const val URL = "jdbc:mysql://tu-servidor:3306/app_dengue"
    private const val USER = "root"
    private const val PASSWORD = "0518"

    fun getConnection(): Connection? {
        return try {
            DriverManager.getConnection(URL, USER, PASSWORD)
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }
}
