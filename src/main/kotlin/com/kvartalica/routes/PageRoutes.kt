package com.kvartalica.routes

import com.kvartalica.dto.PageInfoDto
import com.kvartalica.dto.SocialMediaDto
import com.kvartalica.repository.PageRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.pageRoutes() {
    get("/page_info") {
        try {
            val pageInfo = PageRepository.getPageInfo()
            call.respond(pageInfo)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to get PageInfo: ${e.message}")
        }
    }

    get("/social_media") {
        try {
            val socialMedia = PageRepository.getSocialMedia()
            call.respond(socialMedia)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Failed to get SocialMedia: ${e.message}")
        }
    }

    authenticate("admin-jwt", "content-jwt") {
        put("/page_info") {
            try {
                val pageInfo = call.receive<PageInfoDto>()
                println(pageInfo)
                PageRepository.updatePageInfo(pageInfo)
                call.respond(HttpStatusCode.OK, "PageInfo updated successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update PageInfo: ${e.message}")
            }
        }

        post("/social_media") {
            try {
                val socialMediaDto = call.receive<SocialMediaDto>()
                PageRepository.createSocialMedia(socialMediaDto)
                call.respond(HttpStatusCode.Created, "SocialMedia created successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create SocialMedia: ${e.message}")
            }
        }

        put("/social_media") {
            try {
                val socialMediaDto = call.receive<List<SocialMediaDto>>()
                PageRepository.updateSocialMedia(socialMediaDto)
                call.respond(HttpStatusCode.OK, "SocialMedia updated successfully")
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update SocialMedia: ${e.message}")
            }
        }

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
}