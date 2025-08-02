package com.kvartalica.config

import com.kvartalica.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val url = Config.postgresUrl
    val user = Config.postgresUser
    val password = Config.postgresPassword
    val maxPoolSize = Config.DB_MAX_POOL_SIZE

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