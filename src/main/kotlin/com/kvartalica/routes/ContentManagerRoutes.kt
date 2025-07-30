package com.kvartalica.routes

import com.kvartalica.dto.LoginRequest
import com.kvartalica.dto.PageInfoDto
import com.kvartalica.dto.RegisterRequest
import com.kvartalica.dto.SocialMediaDto
import com.kvartalica.models.UserRole
import com.kvartalica.models.Users
import com.kvartalica.repository.PageRepository
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

        if (contentManager != null && BCrypt.checkpw(request.password, contentManager[Users.password] ?: "")) {
            val token = JwtConfig.generateToken(
                contentManager[Users.id].value.toString(),
                contentManager[Users.email],
                UserRole.CONTENT_MANAGER
            )
            call.respond(mapOf("token" to token, "role" to contentManager[Users.role]))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid content manager credentials")
        }
    }

    post("/content-manager/register") {
        val request = call.receive<RegisterRequest>()
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

    // TODO("PROTECT THIS ROUTE")
    put("/page_info") {
        try {
            val pageInfo = call.receive<PageInfoDto>()
            PageRepository.updatePageInfo(pageInfo)
            call.respond(HttpStatusCode.OK, "PageInfo updated successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to update PageInfo: ${e.message}")
        }
    }
    // TODO("PROTECT THIS ROUTE")
    post("/social_media") {
        try {
            val socialMediaDto = call.receive<SocialMediaDto>()
            PageRepository.createSocialMedia(socialMediaDto)
            call.respond(HttpStatusCode.Created, "SocialMedia created successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to create SocialMedia: ${e.message}")
        }
    }
    // TODO("PROTECT THIS ROUTE")
    put("/social_media") {
        try {
            val socialMediaDto = call.receive<SocialMediaDto>()
            PageRepository.updateSocialMedia(socialMediaDto)
            call.respond(HttpStatusCode.OK, "SocialMedia updated successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to update SocialMedia: ${e.message}")
        }
    }
    // TODO("PROTECT THIS ROUTE")
    delete("/social_media/{id}") {
        try {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                return@delete
            }
            PageRepository.deleteSocialMedia(id)
            call.respond(HttpStatusCode.OK, "SocialMedia deleted successfully")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete SocialMedia: ${e.message}")
        }
    }
}