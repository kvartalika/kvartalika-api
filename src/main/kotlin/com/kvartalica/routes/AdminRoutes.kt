package com.kvartalica.routes

import com.kvartalica.dto.LoginRequest
import com.kvartalica.dto.UserDto
import com.kvartalica.models.UserRole
import com.kvartalica.models.Users
import com.kvartalica.repository.UserRepository
import com.kvartalica.utils.JwtConfig
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt
import java.util.*

fun Route.adminRoutes() {
    post("/admin/login") {
        val request = call.receive<LoginRequest>()
        val admin = transaction {
            Users.selectAll()
                .where { (Users.email eq request.email) and (Users.role eq UserRole.ADMIN.name) }
                .singleOrNull()
        }
        if (admin != null && BCrypt.checkpw(request.password, admin[Users.password])) {
            val accessToken = JwtConfig.generateAccessToken(
                userId = admin[Users.id].toString(),
                username = admin[Users.name],
                role = UserRole.ADMIN
            )
            val refreshToken = JwtConfig.generateRefreshToken(
                userId = admin[Users.id].toString()
            )
            call.respond(
                mapOf(
                    "accessToken" to accessToken,
                    "refreshToken" to refreshToken,
                    "role" to admin[Users.role]
                )
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid admin credentials")
        }
    }


    post("/admin/register") {
        val request = call.receive<UserDto>()
        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

        try {
            val adminId = transaction {
                Users.insert {
                    it[Users.name] = request.name
                    it[Users.surname] = request.surname
                    it[Users.patronymic] = request.patronymic
                    it[Users.email] = request.email
                    it[Users.phone] = request.phone
                    it[Users.password] = hashedPassword
                    it[Users.role] = UserRole.ADMIN.name
                }[Users.id]
            }
            call.respond(HttpStatusCode.Created, mapOf("message" to "Admin created", "id" to adminId.toString()))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "Admin already exists")
        }
    }

    post("/auth/refresh") {
        try {
            val body = call.receive<Map<String, String>>()
            println(body.toString())
            val refreshToken = body["refreshToken"]
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Refresh token required")
            println(refreshToken)
            val decodedJWT = JwtConfig.verifyToken(refreshToken)
            if (decodedJWT == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid refresh token")
                return@post
            }
            val tokenType = decodedJWT.getClaim("type").asString()
            if (tokenType != "refresh") {
                call.respond(HttpStatusCode.Unauthorized, "Invalid token type")
                return@post
            }
            val userId = decodedJWT.subject
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid token payload")
                return@post
            }
            val user = transaction {
                Users.selectAll()
                    .where { Users.id eq UUID.fromString(userId) }
                    .singleOrNull()
            }
            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not found")
                return@post
            }
            val userRole = UserRole.valueOf(user[Users.role])
            val newAccessToken = JwtConfig.generateAccessToken(
                userId = userId,
                username = user[Users.name],
                role = userRole
            )
            val newRefreshToken = JwtConfig.generateRefreshToken(userId)
            call.respond(
                mapOf(
                    "accessToken" to newAccessToken,
                    "refreshToken" to newRefreshToken,
                    "role" to userRole.name
                )
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request format")
        }
    }

    authenticate("admin-jwt", "content-jwt") {
        route("/admin/content-managers") {
            get {
                val managers = UserRepository.getAll().filter { it.role == UserRole.CONTENT_MANAGER.name }
                call.respond(managers)
            }

            post {
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
                        }[Users.id]
                    }
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Content manager created", "id" to "$managerId")
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, "Content manager already exists")
                }
            }

            put("/{email}") {
                val email = call.parameters["email"] ?: ""
                val request = call.receive<UserDto>()
                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
                val updated = transaction {
                    Users.update({ (Users.email eq email) and (Users.role eq UserRole.CONTENT_MANAGER.name) }) {
                        it[Users.name] = request.name
                        it[Users.surname] = request.surname
                        it[Users.patronymic] = request.patronymic
                        it[Users.email] = request.email
                        it[Users.phone] = request.phone
                        it[Users.password] = hashedPassword
                    }
                }
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, "Content manager updated")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Content manager not found")
                }
            }

            delete("/{email}") {
                val email = call.parameters["email"]
                if (email == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid email")
                    return@delete
                }
                try {
                    UserRepository.delete(email)
                    call.respond(HttpStatusCode.OK, "Content manager deleted")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "$e")
                }
            }
        }

        route("/admin/admin") {
            get {
                val managers = UserRepository.getAll()
                call.respond(managers)
            }

            post {
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
                            it[Users.role] = UserRole.ADMIN.name
                        }[Users.id]
                    }
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Admin created", "id" to "$managerId")
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, "Admin already exists")
                }
            }

            put("/{email}") {
                val email = call.parameters["email"] ?: ""
                val request = call.receive<UserDto>()
                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
                val updated = transaction {
                    Users.update({ (Users.email eq email) and (Users.role eq UserRole.ADMIN.name) }) {
                        it[Users.name] = request.name
                        it[Users.surname] = request.surname
                        it[Users.patronymic] = request.patronymic
                        it[Users.email] = request.email
                        it[Users.phone] = request.phone
                        it[Users.password] = hashedPassword
                    }
                }
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, "Content manager updated")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Content manager not found")
                }
            }

            delete("/{email}") {
                val email = call.parameters["email"]
                if (email == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid email")
                    return@delete
                }
                try {
                    UserRepository.delete(email)
                    call.respond(HttpStatusCode.OK, "Content manager deleted")
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.NotFound, "$e")
                }
            }
        }
    }
}