package com.kvartalica.routes

import com.kvartalica.repository.FilesRepository
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.filesRoutes() {
    authenticate("admin-jwt", "content-jwt") {
        route("/directories") {
            get {
                call.respond(mapOf("directories" to FilesRepository.listDirectories()))
            }
            get("/{parent...}") {
                val segments = call.parameters.getAll("parent") ?: emptyList()
                val parent = segments.joinToString(File.separator)
                call.respond(mapOf("children" to FilesRepository.listChildDirectories(parent)))
            }
            post("/{path...}") {
                val segments = call.parameters.getAll("path") ?: emptyList()
                if (segments.isEmpty()) return@post call.respond(HttpStatusCode.BadRequest, "Путь не указан")
                val name = segments.last()
                val parent = if (segments.size > 1) segments.dropLast(1).joinToString(File.separator) else ""
                val fullPath = if (parent.isNotBlank()) "$parent/$name" else name
                if (FilesRepository.createDirectory(fullPath)) {
                    call.respond(HttpStatusCode.Created)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Папка уже существует")
                }
            }
            delete("/{path...}") {
                val path = call.parameters.getAll("path")!!.joinToString(File.separator)
                if (FilesRepository.deleteDirectory(path)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Папка не найдена")
                }
            }
        }
    }

    route("/files") {
        get("/all") {
            val files = FilesRepository.getAllFiles()
            call.respond(files)
        }
        get("/{path...}") {
            val path = call.parameters.getAll("path")!!.joinToString(File.separator)
            val file = FilesRepository.getFile(path)
            if (file != null) call.respondFile(file)
            else call.respond(HttpStatusCode.NotFound, "Файл не найден: $path")
        }
        get("/list/{dir...}") {
            val dir = call.parameters.getAll("dir")!!.joinToString(File.separator)
            call.respond(mapOf("files" to FilesRepository.listFiles(dir)))
        }
        authenticate("admin-jwt", "content-jwt") {
            post("/upload/{dir...}") {
                val dir = call.parameters.getAll("dir")!!.joinToString(File.separator)
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            val bytes = part.streamProvider().readBytes()
                            val fileName = part.originalFileName ?: "file"
                            FilesRepository.saveFile(dir, fileName, bytes)
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                call.respond(HttpStatusCode.Created)
            }
            delete("/{path...}") {
                val path = call.parameters.getAll("path")!!.joinToString(File.separator)
                if (FilesRepository.deleteFile(path)) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Файл не найден")
                }
            }
        }
    }
}