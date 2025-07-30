package com.kvartalica.config

import com.kvartalica.dto.*
import com.kvartalica.models.*
import com.kvartalica.routes.adminRoutes
import com.kvartalica.routes.clientRoutes
import com.kvartalica.routes.contentManagerRoutes
import com.kvartalica.routes.imageRoutes
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant


fun Application.configureRouting() {
    routing {
        contentManagerRoutes()
        adminRoutes()
        clientRoutes()
        imageRoutes()

        post("/requests") {
            val req = call.receive<RequestCreate>()
            val id = transaction {
                Bids.insert {
                    it[name] = req.name
                    it[surname] = req.surname
                    it[email] = req.email
                    it[phone] = req.phone
                } get Bids.id
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        authenticate("content-jwt") {
//            route("/content") {
//                route("/flats") {
//                    post {
//                        val request = call.receive<FlatRequest>()
//                        val newId = transaction {
//                            Flats.insert {
//                                it[Flats.homeId] = request.homeId
//                                it[Flats.flatName] = request.flatName
//                                it[Flats.flatValue] = request.flatValue?.toBigDecimal()
//                                it[Flats.amountOfRooms] = request.amountOfRooms
//                                it[Flats.flatFloor] = request.flatFloor
//                                it[Flats.placementOfFlat] = request.placementOfFlat
//                                it[Flats.amountOfBathrooms] = request.amountOfBathrooms
//                                it[Flats.isDecorated] = request.isDecorated
//                                it[Flats.area] = request.area
//                            }[Flats.flatId]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val request = call.receive<FlatRequest>()
//                        val updated = transaction {
//                            Flats.update({ Flats.flatId eq id }) {
//                                it[Flats.homeId] = request.homeId
//                                it[Flats.flatName] = request.flatName
//                                it[Flats.flatValue] = request.flatValue?.toBigDecimal()
//                                it[Flats.amountOfRooms] = request.amountOfRooms
//                                it[Flats.flatFloor] = request.flatFloor
//                                it[Flats.placementOfFlat] = request.placementOfFlat
//                                it[Flats.amountOfBathrooms] = request.amountOfBathrooms
//                                it[Flats.isDecorated] = request.isDecorated
//                                it[Flats.area] = request.area
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Flat updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Flat not found")
//                        }
//                    }
//
//                    delete("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@delete
//                        }
//
//                        val deleted = transaction {
//                            Flats.deleteWhere { Flats.flatId eq id }
//                        }
//
//                        if (deleted > 0) {
//                            call.respond(HttpStatusCode.OK, "Flat deleted")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Flat not found")
//                        }
//                    }
//                }
//
//                // CRUD дома
//                route("/homes") {
//                    post {
//                        val request = call.receive<HomeRequest>()
//                        val newId = transaction {
//                            Homes.insert {
//                                it[Homes.categoryId] = request.categoryId
//                                it[Homes.homeAddress] = request.homeAddress
//                                it[Homes.yearBuilt] = request.yearBuilt
//                                it[Homes.totalFloors] = request.totalFloors
//                                it[Homes.createdAt] = Instant.now()
//                                it[Homes.updatedAt] = Instant.now()
//                            }[Homes.id]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val request = call.receive<HomeRequest>()
//                        val updated = transaction {
//                            Homes.update({ Homes.id eq id }) {
//                                it[Homes.categoryId] = request.categoryId
//                                it[Homes.homeAddress] = request.homeAddress
//                                it[Homes.yearBuilt] = request.yearBuilt
//                                it[Homes.totalFloors] = request.totalFloors
//                                it[Homes.updatedAt] = Instant.now()
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Home updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Home not found")
//                        }
//                    }
//
//                    delete("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@delete
//                        }
//
//                        val deleted = transaction {
//                            Homes.deleteWhere { Homes.id eq id }
//                        }
//
//                        if (deleted > 0) {
//                            call.respond(HttpStatusCode.OK, "Home deleted")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Home not found")
//                        }
//                    }
//                }
//
//                // CRUD photos
//                route("/photos") {
//                    post {
//                        val multipart = call.receiveMultipart()
//                        var altText: String? = null
//                        var photoBytes: ByteArray? = null
//
//                        multipart.forEachPart { part ->
//                            when (part) {
//                                is PartData.FormItem -> {
//                                    if (part.name == "altText") altText = part.value
//                                }
//
//                                is PartData.FileItem -> {
//                                    if (part.name == "photo") photoBytes = part.streamProvider().readBytes()
//                                }
//
//                                else -> {}
//                            }
//                            part.dispose()
//                        }
//
//                        if (photoBytes == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Photo file is required")
//                            return@post
//                        }
//
//                        val newId = transaction {
//                            Photos.insert {
//                                it[Photos.photo] = photoBytes
//                                it[Photos.altText] = altText
//                                it[Photos.createdAt] = Instant.now()
//                                it[Photos.updatedAt] = Instant.now()
//                            }[Photos.id]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val multipart = call.receiveMultipart()
//                        var altText: String? = null
//                        var photoBytes: ByteArray? = null
//
//                        multipart.forEachPart { part ->
//                            when (part) {
//                                is PartData.FormItem -> {
//                                    if (part.name == "altText") altText = part.value
//                                }
//
//                                is PartData.FileItem -> {
//                                    if (part.name == "photo") photoBytes = part.streamProvider().readBytes()
//                                }
//
//                                else -> {}
//                            }
//                            part.dispose()
//                        }
//
//                        val updated = transaction {
//                            Photos.update({ Photos.id eq id }) {
//                                if (photoBytes != null) it[Photos.photo] = photoBytes
//                                it[Photos.altText] = altText
//                                it[Photos.updatedAt] = Instant.now()
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Photo updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Photo not found")
//                        }
//                    }
//
//                    delete("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@delete
//                        }
//
//                        val deleted = transaction {
//                            Photos.deleteWhere { Photos.id eq id }
//                        }
//
//                        if (deleted > 0) {
//                            call.respond(HttpStatusCode.OK, "Photo deleted")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Photo not found")
//                        }
//                    }
//                }
//
//                // CRUD descriptions
//                route("/descriptions") {
//                    post {
//                        val request = call.receive<DescriptionRequest>()
//                        val newId = transaction {
//                            Descriptions.insert {
//                                it[Descriptions.descriptionText] = request.descriptionText
//                                it[Descriptions.createdAt] = Instant.now()
//                                it[Descriptions.updatedAt] = Instant.now()
//                            }[Descriptions.id]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val request = call.receive<DescriptionRequest>()
//                        val updated = transaction {
//                            Descriptions.update({ Descriptions.id eq id }) {
//                                it[Descriptions.descriptionText] = request.descriptionText
//                                it[Descriptions.updatedAt] = Instant.now()
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Description updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Description not found")
//                        }
//                    }
//
//                    delete("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@delete
//                        }
//
//                        val deleted = transaction {
//                            Descriptions.deleteWhere { Descriptions.id eq id }
//                        }
//
//                        if (deleted > 0) {
//                            call.respond(HttpStatusCode.OK, "Description deleted")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Description not found")
//                        }
//                    }
//                }
//
//                // CRUD категории
//                route("/categories") {
//                    post {
//                        val request = call.receive<CategoryRequest>()
//                        val newId = transaction {
//                            Categories.insert {
//                                it[Categories.categoryName] = request.categoryName
//                            }[Categories.id]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val request = call.receive<CategoryRequest>()
//                        val updated = transaction {
//                            Categories.update({ Categories.id eq id }) {
//                                it[Categories.categoryName] = request.categoryName
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Category updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Category not found")
//                        }
//                    }
//
//                    delete("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@delete
//                        }
//
//                        val deleted = transaction {
//                            Categories.deleteWhere { Categories.id eq id }
//                        }
//
//                        if (deleted > 0) {
//                            call.respond(HttpStatusCode.OK, "Category deleted")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Category not found")
//                        }
//                    }
//                }
//
//                // CRUD footer
//                route("/footer") {
//                    post {
//                        val request = call.receive<PageInfoDto>()
//                        val newId = transaction {
//                            PageInfo.insert {
//                                it[PageInfo.companyName] = request.companyName
//                                it[PageInfo.placementOfSelling] = request.placementOfSelling
//                                it[PageInfo.companyPhone] = request.companyPhone
//                                it[PageInfo.companyEmail] = request.companyEmail
//                                it[PageInfo.socialLinks] = request.socialLinks
//                            }[PageInfo.id]
//                        }
//                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
//                    }
//
//                    put("/{id}") {
//                        val id = call.parameters["id"]?.toIntOrNull()
//                        if (id == null) {
//                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
//                            return@put
//                        }
//
//                        val request = call.receive<PageInfoDto>()
//                        val updated = transaction {
//                            PageInfo.update({ PageInfo.id eq id }) {
//                                it[PageInfo.companyName] = request.companyName
//                                it[PageInfo.placementOfSelling] = request.placementOfSelling
//                                it[PageInfo.companyPhone] = request.companyPhone
//                                it[PageInfo.companyEmail] = request.companyEmail
//                                it[PageInfo.socialLinks] = request.socialLinks
//                            }
//                        }
//
//                        if (updated > 0) {
//                            call.respond(HttpStatusCode.OK, "Footer updated")
//                        } else {
//                            call.respond(HttpStatusCode.NotFound, "Footer not found")
//                        }
//                    }
//                }
//            }
        }
    }
}