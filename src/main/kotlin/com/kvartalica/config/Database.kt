package com.kvartalica.config

import com.kvartalica.models.Bids
import com.kvartalica.models.Categories
import com.kvartalica.models.Descriptions
import com.kvartalica.models.Flats
import com.kvartalica.models.Footer
import com.kvartalica.models.Homes
import com.kvartalica.models.Photos
import com.kvartalica.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val env = dotenv()

    val url = env["DB_URL"]
    val user = env["DB_USER"]
    val password = env["DB_PASSWORD"]
    val maxPoolSize = env["DB_MAX_POOL_SIZE"]?.toIntOrNull() ?: 10

    val hikariConfig = HikariConfig().apply {
        jdbcUrl = url
        username = user
        this.password = password
        maximumPoolSize = maxPoolSize
    }
    val dataSource = HikariDataSource(hikariConfig)

    Database.connect(dataSource)
    transaction {
        SchemaUtils.create(Categories, Homes, Flats, Footer, Users, Photos, Descriptions, Bids)
    }
    try {
        transaction {
            exec("SELECT 1;")
            println("Database connection successful.")
        }
    } catch (e: Exception) {
        println("Database connection failed: ${e.message}")
    }
}