package com.kvartalica.utils

import com.kvartalica.config.Config
import com.kvartalica.models.Users
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun initAdmin() {
    val name = Config.initName
    val surname = Config.initSurname
    val email = Config.initEmail
    val password = Config.initPassword
    val telegramId = Config.initTelegramId

    val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

    transaction {
        val exists = Users
            .selectAll()
            .where { Users.email eq email }
            .any()
        if (!exists) {
            Users.insert {
                it[Users.name] = name
                it[Users.surname] = surname
                it[Users.patronymic] = null
                it[Users.email] = email
                it[Users.phone] = null
                it[Users.role] = "ADMIN"
                it[Users.password] = hashedPassword
                it[Users.telegramId] = telegramId
            }
        } else {
            println("Admin user already exists: $email")
        }
    }
}