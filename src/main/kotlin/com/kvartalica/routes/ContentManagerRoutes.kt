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
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun Route.contentManagerRoutes() {
    post("/content-manager/login") {
        val request = call.receive<LoginRequest>()
        val contentManager = transaction {
            Users.select {
                (Users.email eq request.email) and
                        (Users.role eq UserRole.CONTENT_MANAGER.name)
            }.singleOrNull()
        }

        if (contentManager != null && BCrypt.checkpw(request.password, contentManager[Users.password])) {
            val accessToken = JwtConfig.generateAccessToken(
                userId = contentManager[Users.id].toString(),
                username = contentManager[Users.name],
                role = UserRole.CONTENT_MANAGER
            )
            val refreshToken = JwtConfig.generateRefreshToken(
                userId = contentManager[Users.id].toString()
            )

            call.response.cookies.append(
                Cookie(
                    name = "refreshToken",
                    value = refreshToken,
                    maxAge = 60 * 60 * 24 * 30,
                    path = "/",
                    httpOnly = true,
                    secure = false,
                    // extensions = mapOf("SameSite" to "None")
                )
            )

            call.respond(
                mapOf(
                    "accessToken" to accessToken,
                    "role" to UserRole.CONTENT_MANAGER.name
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