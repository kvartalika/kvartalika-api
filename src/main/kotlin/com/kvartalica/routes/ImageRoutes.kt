package com.kvartalica.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.filesRoutes() {
    get("/images/{directory}/{id}") {
        val id = call.parameters["id"]
        val directory = call.parameters["directory"]
        if (id == null || directory == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing id or directory")
            return@get
        }
        val imageFile = File("static/images/$directory/$id")
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
                val dir = File("static/images/$directory")
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
    // TODO("PROTECT THIS ROUTE")
    put("/images/{directory}/{id}") {
        val directory = call.parameters["directory"]
        val id = call.parameters["id"]
        if (directory == null || id == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing directory or id")
            return@put
        }
        val dir = File("static/images/$directory")
        val targetFile = File(dir, "$id.png")
        if (!targetFile.exists()) {
            call.respond(HttpStatusCode.NotFound, "Image to update not found")
            return@put
        }
        val multipart = call.receiveMultipart()
        var updated = false
        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                if (!dir.exists()) dir.mkdirs()
                part.streamProvider().use { input ->
                    targetFile.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }
                updated = true
            }
            part.dispose()
        }
        if (!updated) {
            call.respond(HttpStatusCode.BadRequest, "No valid PNG file uploaded for update")
        } else {
            call.respond(HttpStatusCode.OK, "Image updated successfully")
        }
    }
    // TODO("PROTECT THIS ROUTE")
    delete("/images/{directory}/{id}") {
        val directory = call.parameters["directory"]
        val id = call.parameters["id"]
        if (directory == null || id == null) {
            call.respond(HttpStatusCode.BadRequest, "Missing directory or id")
            return@delete
        }
        val imageFile = File("static/images/$directory/$id.png")
        try {
            if (!imageFile.exists() || !imageFile.isFile) {
                call.respond(HttpStatusCode.NotFound, "Image not found")
            } else {
                if (imageFile.delete()) {
                    call.respond(HttpStatusCode.OK, "Image deleted successfully")
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to delete image")
                }
            }
        } catch (e: Exception) {
            call.application.environment.log.error("Failed to delete image", e)
            call.respond(HttpStatusCode.InternalServerError, "Failed to delete image")
        }
    }
    get("/images") {
        val directories = listOf("flats", "history", "homes", "layouts", "media")
        val basePath = File("static/images")
        val imagePaths = directories.flatMap { dirName ->
            val dir = File(basePath, dirName)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()
                    ?.filter { it.isFile }
                    ?.map { file -> "/images/$dirName/${file.name}" }
                    ?: emptyList()
            } else {
                emptyList()
            }
        }
        call.respond(imagePaths)
    }
}