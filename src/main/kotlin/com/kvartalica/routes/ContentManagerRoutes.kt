package com.kvartalica.routes

import com.kvartalica.dto.LoginRequest
import com.kvartalica.dto.UserDto
import com.kvartalica.models.UserRole
import com.kvartalica.models.Users
import com.kvartalica.utils.JwtConfig
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.contentManagerRoutes() {
    post("/content-manager/login") {
        val request = call.receive<LoginRequest>()
        val contentManager = transaction {
            Users.selectAll()
                .where { (Users.email eq request.email) and (Users.role eq UserRole.CONTENT_MANAGER.name) }
                .singleOrNull()
        }
        if (contentManager != null && BCrypt.checkpw(request.password, contentManager[Users.password])) {
            val accessToken = JwtConfig.generateAccessToken(
                userId = contentManager[Users.id].toString(),
                username = contentManager[Users.name],
                role = UserRole.ADMIN
            )
            val refreshToken = JwtConfig.generateRefreshToken(
                userId = contentManager[Users.id].toString()
            )
            call.respond(
                mapOf(
                    "accessToken" to accessToken,
                    "refreshToken" to refreshToken,
                    "role" to contentManager[Users.role]
                )
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid content manager credentials")
        }
    }

    post("/content-manager/register") {
        val request = call.receive<UserDto>()
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        try {
            val managerId = transaction {
                Users.insert {
                    it[Users.name] = request.name
                    it[Users.surname] = request.surname
                    it[Users.patronymic] = request.patronymic
                    it[Users.email] = request.email
                    it[Users.phone] = request.phone
                    it[Users.password] = hashedPassword
                    it[Users.role] = UserRole.CONTENT_MANAGER.name
                    it[Users.telegramId] = request.telegramId
                }[Users.id]
            }
            call.respond(
                HttpStatusCode.Created,
                mapOf(
                    "message" to "Content manager created",
                    "id" to managerId.toString()
                )
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "Content manager already exists")
        }
    }
}