package com.kvartalica.routes

import com.kvartalica.dto.FlatDto
import com.kvartalica.repository.CategoriesRepository
import com.kvartalica.repository.FlatRepository
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.flatRoutes() {
    route("/flats") {
        post("/{id}/categories/{c_id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val c_id = call.parameters["c_id"]?.toIntOrNull()
            if (id == null || c_id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@post
            }
            CategoriesRepository.addCategoryToFlat(id, c_id)
            call.respondText("Flat added", status = io.ktor.http.HttpStatusCode.OK)
        }
        delete("/{id}/categories/{c_id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val c_id = call.parameters["c_id"]?.toIntOrNull()
            if (id == null || c_id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@delete
            }
            CategoriesRepository.deleteCategoryFromFlat(id, c_id)
            call.respondText("Flat deleted", status = io.ktor.http.HttpStatusCode.OK)
        }
        get("/categories/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val flats = FlatRepository.getFlatsByCategory(id)
            call.respond(flats)
        }
        get {
            val flats = FlatRepository.getAll()
            call.respond(flats)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@get
            }
            val flat = FlatRepository.getById(id)
            if (flat == null) {
                call.respondText("Flat not found", status = io.ktor.http.HttpStatusCode.NotFound)
            } else {
                call.respond(flat)
            }
        }
        post {
            val dto = call.receive<FlatDto>()
            FlatRepository.create(dto)
            call.respondText("Flat created", status = io.ktor.http.HttpStatusCode.Created)
        }
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@put
            }
            val dto = call.receive<FlatDto>()
            FlatRepository.update(id, dto)
            call.respondText("Flat updated", status = io.ktor.http.HttpStatusCode.OK)
        }
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respondText("Invalid or missing id", status = io.ktor.http.HttpStatusCode.BadRequest)
                return@delete
            }
            FlatRepository.deleteFlat(id)
            call.respondText("Flat deleted", status = io.ktor.http.HttpStatusCode.OK)
        }
    }
}