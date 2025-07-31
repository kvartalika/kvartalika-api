package com.kvartalica.routes

import com.kvartalica.dto.HomeDto
import com.kvartalica.repository.FlatRepository
import com.kvartalica.repository.HomeRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homeRoutes() {
    route("/homes") {
        post {
            val dto = call.receive<HomeDto>()
            HomeRepository.create(dto)
            call.respond(HttpStatusCode.Created, "Home created")
        }

        get {
            val homes = HomeRepository.getAll()
            call.respond(homes)
        }

        get("{id}/flats") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@get
            }
            val flats = FlatRepository.getFlatsByHome(id)
            call.respond(flats)
        }

        get("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@get
            }
            val home = HomeRepository.getById(id)
            if (home == null) {
                call.respond(HttpStatusCode.NotFound, "Home not found")
            } else {
                call.respond(home)
            }
        }

        put("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@put
            }
            val dto = call.receive<HomeDto>()
            HomeRepository.update(id, dto)
            call.respond(HttpStatusCode.OK, "Home updated")
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid id")
                return@delete
            }
            HomeRepository.delete(id)
            call.respond(HttpStatusCode.OK, "Home deleted")
        }
    }
}