package com.kvartalica.routes

import com.kvartalica.dto.SearchDto
import com.kvartalica.repository.FlatRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.clientRoutes() {
    post("/search") {
        try {
            val searchDto = call.receive<SearchDto>()
            val results = FlatRepository.search(searchDto)
            call.respond(results)
        } catch (e: ContentTransformationException) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Invalid search request format: ${e.localizedMessage}")
            )
        } catch (e: Exception) {
            application.log.error("Error during search", e)
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }
    }
}