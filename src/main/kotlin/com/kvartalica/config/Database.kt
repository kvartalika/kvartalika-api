package com.kvartalica.config

import com.kvartalica.models.*
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
//        exec("CREATE SCHEMA public;")
        SchemaUtils.create(Bids, Categories, FlatCategories, Flats, Homes, PageInfo, SocialMedia, Users)
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