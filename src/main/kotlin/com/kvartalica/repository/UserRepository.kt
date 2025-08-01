package com.kvartalica.repository

import com.kvartalica.dto.UserDto
import com.kvartalica.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

object UserRepository {
    fun create(userDto: UserDto) {
        transaction {
            Users.insert {
                it[name] = userDto.name
                it[surname] = userDto.surname
                it[patronymic] = userDto.patronymic
                it[email] = userDto.email
                it[phone] = userDto.phone
                it[role] = userDto.role
                it[password] = userDto.password
                it[createdAt] = LocalDateTime.now()
                it[updatedAt] = LocalDateTime.now()
                it[telegramId] = userDto.telegramId
            }
        }
    }

    fun getAll(): List<UserDto> = transaction {
        Users.selectAll().map {
            UserDto(
                name = it[Users.name],
                surname = it[Users.surname],
                patronymic = it[Users.patronymic],
                email = it[Users.email],
                phone = it[Users.phone],
                createdAt = it[Users.createdAt].toString(),
                updatedAt = it[Users.updatedAt].toString(),
                role = it[Users.role],
                password = it[Users.password],
                telegramId = it[Users.telegramId],
            )
        }
    }

    fun getById(id: UUID): UserDto? = transaction {
        Users.select { Users.id eq id }
            .mapNotNull {
                UserDto(
                    name = it[Users.name],
                    surname = it[Users.surname],
                    patronymic = it[Users.patronymic],
                    email = it[Users.email],
                    phone = it[Users.phone],
                    createdAt = it[Users.createdAt].toString(),
                    updatedAt = it[Users.updatedAt].toString(),
                    role = it[Users.role],
                    password = it[Users.password],
                    telegramId = it[Users.telegramId],
                )
            }.singleOrNull()
    }

    fun update(id: UUID, userDto: UserDto) {
        transaction {
            Users.update({ Users.id eq id }) {
                it[name] = userDto.name
                it[surname] = userDto.surname
                it[patronymic] = userDto.patronymic
                it[email] = userDto.email
                it[phone] = userDto.phone
                it[role] = userDto.role
                it[password] = userDto.password
                it[updatedAt] = LocalDateTime.now()
                it[telegramId] = userDto.telegramId
            }
        }
    }

    fun delete(email: String) {
        transaction {
            Users.deleteWhere { Users.email eq email }
        }
    }

    fun getAllTelegramUsers(): List<String> {
        return transaction {
            Users.selectAll()
                .where { Users.telegramId.isNotNull() }
                .mapNotNull { it[Users.telegramId] }
        }
    }
}