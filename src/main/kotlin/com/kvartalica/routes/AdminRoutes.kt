package com.kvartalica.routes

import com.kvartalica.dto.ContentManagerRequest
import com.kvartalica.dto.LoginRequest
import com.kvartalica.dto.RegisterRequest
import com.kvartalica.models.UserRole
import com.kvartalica.models.Users
import com.kvartalica.utils.JwtConfig
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
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

        if (admin != null && BCrypt.checkpw(request.password, admin[Users.password] ?: "")) {
            val token = JwtConfig.generateToken(
                admin[Users.id].toString(),
                admin[Users.email],
                UserRole.ADMIN
            )
            call.respond(mapOf("token" to token, "role" to admin[Users.role]))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid admin credentials")
        }
    }

    post("/admin/register") {
        val request = call.receive<RegisterRequest>()
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

    post("/admin/setup") {
        val adminName = "Admin"
        val adminSurname = "User"
        val adminEmail = "admin@example.com"
        val adminPassword = "admin123"
        val hashedPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt())

        try {
            val adminId = transaction {
                Users.insert {
                    it[Users.name] = adminName
                    it[Users.surname] = adminSurname
                    it[Users.email] = adminEmail
                    it[Users.password] = hashedPassword
                    it[Users.role] = UserRole.ADMIN.name
                }[Users.id]
            }
            call.respond(
                HttpStatusCode.Created,
                mapOf("message" to "Initial admin setup completed", "id" to adminId.toString())
            )
        } catch (e: Exception) {
            call.respond(HttpStatusCode.Conflict, "Admin already exists")
        }
    }

    authenticate("admin-jwt") {
        route("/admin/content-managers") {
            get {
                val managers = transaction {
                    Users.selectAll().where { Users.role eq UserRole.CONTENT_MANAGER.name }
                        .map { row ->
                            mapOf(
                                "id" to row[Users.id].toString(),
                                "name" to row[Users.name],
                                "surname" to row[Users.surname],
                                "patronymic" to row[Users.patronymic],
                                "email" to row[Users.email],
                                "phone" to row[Users.phone]
                            )
                        }
                }
                call.respond(managers)
            }

            post {
                val request = call.receive<ContentManagerRequest>()
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
                        mapOf("message" to "Content manager created", "id" to managerId.toString())
                    )
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.Conflict, "Content manager already exists")
                }
            }

            put("/{id}") {
                val idString = call.parameters["id"]
                val id = try {
                    UUID.fromString(idString)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid UUID")
                    return@put
                }

                val request = call.receive<ContentManagerRequest>()
                val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

                val updated = transaction {
                    Users.update({ (Users.id eq id) and (Users.role eq UserRole.CONTENT_MANAGER.name) }) {
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

            delete("/{id}") {
                val idString = call.parameters["id"]
                val id = try {
                    UUID.fromString(idString)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid UUID")
                    return@delete
                }

                val deleted = transaction {
                    Users.deleteWhere { (Users.id eq id) and (Users.role eq UserRole.CONTENT_MANAGER.name) }
                }

                if (deleted > 0) {
                    call.respond(HttpStatusCode.OK, "Content manager deleted")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Content manager not found")
                }
            }
        }
    }
}