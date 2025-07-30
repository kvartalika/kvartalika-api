package com.kvartalica.routes

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.imageRoutes() {
    get("/images/{directory}/{id}") {
        val id = call.parameters["id"]
        val directory = call.parameters["directory"]
        if (id == null || directory == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing id or directory")
            return@get
        }
        val imageFile = File("images/$directory/$id")
        try {
            if (!imageFile.exists() || !imageFile.isFile) {
                call.respond(HttpStatusCode.NotFound, "Image not found")
            } else {
                call.respondFile(imageFile)
            }
        } catch (e: Exception) {
            call.application.environment.log.error("Failed to serve image", e)
            call.respond(HttpStatusCode.InternalServerError, "Failed to serve image")
        }
    }
    // TODO("PROTECT THIS ROUTE")
    post("/images/{directory}/{id}") {
        val directory = call.parameters["directory"]
        val id = call.parameters["id"]
        if (id == null || directory == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing id or directory")
            return@post
        }
        val multipart = call.receiveMultipart()
        var uploaded = false

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val dir = File("images/$directory")
                if (!dir.exists()) dir.mkdirs()

                val savedFile = File(dir, "$id.png")
                part.streamProvider().use { input ->
                    savedFile.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }
                uploaded = true
            }
            part.dispose()
        }
        if (!uploaded) {
            call.respond(HttpStatusCode.BadRequest, "No valid PNG file uploaded")
        } else {
            call.respond(HttpStatusCode.OK, "Image uploaded successfully")
        }
    }
}