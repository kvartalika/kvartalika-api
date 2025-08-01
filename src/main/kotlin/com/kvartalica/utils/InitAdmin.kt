package com.kvartalica.utils

import com.kvartalica.models.Users
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun initAdmin() {
    val env = dotenv()
    val name = env["INIT_NAME"]
    val surname = env["INIT_SURNAME"]
    val email = env["INIT_EMAIL"]
    val password = env["INIT_PASSWORD"]
    val telegramId = env["INIT_TELEGRAM_ID"]

    val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())

    transaction {
        val exists = Users.select { Users.email eq email }.any()
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