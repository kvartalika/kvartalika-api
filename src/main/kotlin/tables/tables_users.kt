package com.example.tables

import com.example.models.*
import kvartalica.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.util.*

class UserDAO {

    fun getAllUsers(): List<User> = transaction {
        Users.selectAll().map { toUser(it) }
    }

    fun getUserById(id: String): User? = transaction {
        Users.selectAll().where { Users.id eq UUID.fromString(id) }
            .mapNotNull { toUser(it) }
            .singleOrNull()
    }

    fun getUserByEmail(email: String): User? = transaction {
        Users.selectAll().where { Users.userEmail eq email }
            .mapNotNull { toUser(it) }
            .singleOrNull()
    }

    fun createUser(request: CreateUserRequest): User = transaction {
        val userId = UUID.randomUUID()
        val now = LocalDateTime.now()

        Users.insert {
            it[id] = userId
            it[username] = request.username
            it[userSurname] = request.userSurname
            it[userPatronymic] = request.userPatronymic
            it[userPhone] = request.userPhone
            it[userEmail] = request.userEmail
            it[createdAt] = now
            it[updatedAt] = now
        }

        User(
            userId = userId.toString(),
            username = request.username,
            userSurname = request.userSurname,
            userPatronymic = request.userPatronymic,
            userPhone = request.userPhone,
            userEmail = request.userEmail,
            createdAt = now.toString(),
            updatedAt = now.toString()
        )
    }

    fun updateUser(id: String, request: UpdateUserRequest): User? = transaction {
        val userId = UUID.fromString(id)
        val now = LocalDateTime.now()

        val updateCount = Users.update({ Users.id eq userId }) {
            request.username?.let { value -> it[username] = value }
            request.userSurname?.let { value -> it[userSurname] = value }
            request.userPatronymic?.let { value -> it[userPatronymic] = value }
            request.userPhone?.let { value -> it[userPhone] = value }
            request.userEmail?.let { value -> it[userEmail] = value }
            it[updatedAt] = now
        }

        if (updateCount > 0) getUserById(id) else null
    }

    fun deleteUser(id: String): Boolean = transaction {
        val userId = UUID.fromString(id)
        Users.deleteWhere { Users.id eq userId } > 0
    }

    fun getUsersWithPagination(page: Int, pageSize: Int): Pair<List<User>, Int> = transaction {
        val offset = (page - 1) * pageSize
        val users = Users.selectAll()
            .limit(pageSize, offset.toLong())
            .map { toUser(it) }

        val totalCount = Users.selectAll().count().toInt()

        Pair(users, totalCount)
    }

    private fun toUser(row: ResultRow): User = User(
        userId = row[Users.id].toString(),
        username = row[Users.username],
        userSurname = row[Users.userSurname],
        userPatronymic = row[Users.userPatronymic],
        userPhone = row[Users.userPhone],
        userEmail = row[Users.userEmail],
        createdAt = row[Users.createdAt].toString(),
        updatedAt = row[Users.updatedAt].toString()
    )
}