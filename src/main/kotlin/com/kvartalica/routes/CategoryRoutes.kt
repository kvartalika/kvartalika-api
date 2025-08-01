package com.kvartalica.routes

import com.kvartalica.dto.CategoryDto
import com.kvartalica.repository.CategoriesRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.categoryRoutes() {
    route("/categories") {
        get {
            val categories = CategoriesRepository.getCategories()
            call.respond(categories)
        }
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                return@get
            }
            val category = CategoriesRepository.getCategory(id)
            if (category == null) {
                call.respond(HttpStatusCode.NotFound, "Category not found")
            } else {
                call.respond(category)
            }
        }
        authenticate("admin-jwt", "content-jwt") {
            post {
                val dto = call.receive<CategoryDto>()
                CategoriesRepository.addCategory(dto)
                call.respond(HttpStatusCode.Created)
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                    return@put
                }
                val existing = CategoriesRepository.getCategory(id)
                if (existing == null) {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                    return@put
                }
                val dto = call.receive<CategoryDto>()
                CategoriesRepository.updateCategory(id, dto)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                    return@delete
                }
                val existing = CategoriesRepository.getCategory(id)
                if (existing == null) {
                    call.respond(HttpStatusCode.NotFound, "Category not found")
                    return@delete
                }
                CategoriesRepository.deleteCategory(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}