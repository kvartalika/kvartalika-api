package com.kvartalica.config

import com.kvartalica.dto.*
import com.kvartalica.models.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.util.*

object Requests : IntIdTable("bids") {
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val email = varchar("email", 100)
    val phone = varchar("phone", 100)
}

fun Application.configureRouting() {
    routing {
        post("/requests") {
            val req = call.receive<RequestCreate>()
            val id = transaction {
                Requests.insert {
                    it[name] = req.name
                    it[surname] = req.surname
                    it[email] = req.email
                    it[phone] = req.phone
                } get Requests.id
            }
            call.respond(HttpStatusCode.Created, mapOf("id" to id.value))
        }

        // ADMIN эндпоинты для входа и регистрации
        post("/admin/login") {
            val request = call.receive<LoginRequest>()
            val admin = transaction {
                Users.selectAll()
                    .where { (Users.email eq request.email) and (Users.role eq UserRole.ADMIN.name) }
                    .singleOrNull()
            }

            if (admin != null && BCrypt.checkpw(request.password, admin[Users.password] ?: "")) {
                val token = JwtConfig.generateToken(
                    admin[Users.id].toString(), // Убираем .value
                    admin[Users.email],
                    UserRole.ADMIN
                )
                call.respond(mapOf("token" to token, "role" to admin[Users.role]))
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Invalid admin credentials")
            }
        }

        post("/admin/register") {
            val request = call.receive<RegisterRequest>()
            val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

            try {
                val adminId = transaction {
                    Users.insert {
                        it[Users.name] = request.name
                        it[Users.surname] = request.surname
                        it[Users.patronymic] = request.patronymic
                        it[Users.email] = request.email
                        it[Users.phone] = request.phone
                        it[Users.password] = hashedPassword
                        it[Users.role] = UserRole.ADMIN.name
                        it[Users.createdAt] = Instant.now()
                        it[Users.updatedAt] = Instant.now()
                    }[Users.id]
                }
                call.respond(HttpStatusCode.Created, mapOf("message" to "Admin created", "id" to adminId.toString()))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "Admin already exists")
            }
        }

        // CONTENT_MANAGER эндпоинты для входа и регистрации
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
                        it[Users.createdAt] = Instant.now()
                        it[Users.updatedAt] = Instant.now()
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

        // CLIENT эндпоинты
        get("/categories") {
            val categories = transaction {
                Categories.selectAll().map { row ->
                    mapOf(
                        "id" to row[Categories.id],
                        "categoryName" to row[Categories.categoryName]
                    )
                }
            }
            call.respond(categories)
        }

        get("/homes") {
            val homes = transaction {
                Homes.selectAll().map { row ->
                    mapOf(
                        "id" to row[Homes.id],
                        "categoryId" to row[Homes.categoryId],
                        "homeAddress" to row[Homes.homeAddress],
                        "yearBuilt" to row[Homes.yearBuilt],
                        "totalFloors" to row[Homes.totalFloors],
                        "ownerId" to row[Homes.ownerId]
                    )
                }
            }
            call.respond(homes)
        }

        get("/flats") {
            val flats = transaction {
                Flats.selectAll().map { row ->
                    mapOf(
                        "flatId" to row[Flats.flatId],
                        "homeId" to row[Flats.homeId],
                        "flatName" to row[Flats.flatName],
                        "flatValue" to row[Flats.flatValue],
                        "amountOfRooms" to row[Flats.amountOfRooms],
                        "flatFloor" to row[Flats.flatFloor],
                        "placementOfFlat" to row[Flats.placementOfFlat],
                        "amountOfBathrooms" to row[Flats.amountOfBathrooms],
                        "isDecorated" to row[Flats.isDecorated],
                        "area" to row[Flats.area]
                    )
                }
            }
            call.respond(flats)
        }

        post("/search") {
            val request = call.receive<SearchRequest>()
            val homes = transaction {
                val query = Homes.selectAll()

                request.categoryId?.let { categoryId ->
                    query.andWhere { Homes.categoryId eq categoryId }
                }

                query.map { row ->
                    mapOf(
                        "id" to row[Homes.id],
                        "categoryId" to row[Homes.categoryId],
                        "homeAddress" to row[Homes.homeAddress],
                        "yearBuilt" to row[Homes.yearBuilt],
                        "totalFloors" to row[Homes.totalFloors]
                    )
                }
            }
            call.respond(homes)
        }

        get("/photos") {
            val photos = transaction {
                Photos.selectAll().map { row ->
                    mapOf(
                        "id" to row[Photos.id],
                        "altText" to row[Photos.altText],
                        "image" to row[Photos.photo]?.let {
                            java.util.Base64.getEncoder().encodeToString(it)
                        }
                    )
                }
            }
            call.respond(photos)
        }

        get("/descriptions") {
            val descriptions = transaction {
                Descriptions.selectAll().map { row ->
                    mapOf(
                        "id" to row[Descriptions.id],
                        "descriptionText" to row[Descriptions.descriptionText]
                    )
                }
            }
            call.respond(descriptions)
        }

        get("/footer") {
            val footer = transaction {
                Footer.selectAll().map { row ->
                    mapOf(
                        "id" to row[Footer.id],
                        "companyName" to row[Footer.companyName],
                        "placementOfSelling" to row[Footer.placementOfSelling],
                        "companyPhone" to row[Footer.companyPhone],
                        "companyEmail" to row[Footer.companyEmail],
                        "socialLinks" to row[Footer.socialLinks]
                    )
                }
            }
            call.respond(footer)
        }

        // CONTENT_MANAGER эндпоинты
        authenticate("content-jwt") {
            route("/content") {
                // CRUD квартиры
                route("/flats") {
                    post {
                        val request = call.receive<FlatRequest>()
                        val newId = transaction {
                            Flats.insert {
                                it[Flats.homeId] = request.homeId
                                it[Flats.flatName] = request.flatName
                                it[Flats.flatValue] = request.flatValue?.toBigDecimal()
                                it[Flats.amountOfRooms] = request.amountOfRooms
                                it[Flats.flatFloor] = request.flatFloor
                                it[Flats.placementOfFlat] = request.placementOfFlat
                                it[Flats.amountOfBathrooms] = request.amountOfBathrooms
                                it[Flats.isDecorated] = request.isDecorated
                                it[Flats.area] = request.area
                            }[Flats.flatId]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val request = call.receive<FlatRequest>()
                        val updated = transaction {
                            Flats.update({ Flats.flatId eq id }) {
                                it[Flats.homeId] = request.homeId
                                it[Flats.flatName] = request.flatName
                                it[Flats.flatValue] = request.flatValue?.toBigDecimal()
                                it[Flats.amountOfRooms] = request.amountOfRooms
                                it[Flats.flatFloor] = request.flatFloor
                                it[Flats.placementOfFlat] = request.placementOfFlat
                                it[Flats.amountOfBathrooms] = request.amountOfBathrooms
                                it[Flats.isDecorated] = request.isDecorated
                                it[Flats.area] = request.area
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Flat updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Flat not found")
                        }
                    }

                    delete("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@delete
                        }

                        val deleted = transaction {
                            Flats.deleteWhere { Flats.flatId eq id }
                        }

                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Flat deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Flat not found")
                        }
                    }
                }

                // CRUD дома
                route("/homes") {
                    post {
                        val request = call.receive<HomeRequest>()
                        val newId = transaction {
                            Homes.insert {
                                it[Homes.categoryId] = request.categoryId
                                it[Homes.homeAddress] = request.homeAddress
                                it[Homes.yearBuilt] = request.yearBuilt
                                it[Homes.totalFloors] = request.totalFloors
                                it[Homes.createdAt] = Instant.now()
                                it[Homes.updatedAt] = Instant.now()
                            }[Homes.id]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val request = call.receive<HomeRequest>()
                        val updated = transaction {
                            Homes.update({ Homes.id eq id }) {
                                it[Homes.categoryId] = request.categoryId
                                it[Homes.homeAddress] = request.homeAddress
                                it[Homes.yearBuilt] = request.yearBuilt
                                it[Homes.totalFloors] = request.totalFloors
                                it[Homes.updatedAt] = Instant.now()
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Home updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Home not found")
                        }
                    }

                    delete("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@delete
                        }

                        val deleted = transaction {
                            Homes.deleteWhere { Homes.id eq id }
                        }

                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Home deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Home not found")
                        }
                    }
                }

                // CRUD photos
                route("/photos") {
                    post {
                        val multipart = call.receiveMultipart()
                        var altText: String? = null
                        var photoBytes: ByteArray? = null

                        multipart.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    if (part.name == "altText") altText = part.value
                                }

                                is PartData.FileItem -> {
                                    if (part.name == "photo") photoBytes = part.streamProvider().readBytes()
                                }

                                else -> {}
                            }
                            part.dispose()
                        }

                        if (photoBytes == null) {
                            call.respond(HttpStatusCode.BadRequest, "Photo file is required")
                            return@post
                        }

                        val newId = transaction {
                            Photos.insert {
                                it[Photos.photo] = photoBytes
                                it[Photos.altText] = altText
                                it[Photos.createdAt] = Instant.now()
                                it[Photos.updatedAt] = Instant.now()
                            }[Photos.id]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val multipart = call.receiveMultipart()
                        var altText: String? = null
                        var photoBytes: ByteArray? = null

                        multipart.forEachPart { part ->
                            when (part) {
                                is PartData.FormItem -> {
                                    if (part.name == "altText") altText = part.value
                                }

                                is PartData.FileItem -> {
                                    if (part.name == "photo") photoBytes = part.streamProvider().readBytes()
                                }

                                else -> {}
                            }
                            part.dispose()
                        }

                        val updated = transaction {
                            Photos.update({ Photos.id eq id }) {
                                if (photoBytes != null) it[Photos.photo] = photoBytes
                                it[Photos.altText] = altText
                                it[Photos.updatedAt] = Instant.now()
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Photo updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Photo not found")
                        }
                    }

                    delete("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@delete
                        }

                        val deleted = transaction {
                            Photos.deleteWhere { Photos.id eq id }
                        }

                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Photo deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Photo not found")
                        }
                    }
                }

                // CRUD descriptions
                route("/descriptions") {
                    post {
                        val request = call.receive<DescriptionRequest>()
                        val newId = transaction {
                            Descriptions.insert {
                                it[Descriptions.descriptionText] = request.descriptionText
                                it[Descriptions.createdAt] = Instant.now()
                                it[Descriptions.updatedAt] = Instant.now()
                            }[Descriptions.id]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val request = call.receive<DescriptionRequest>()
                        val updated = transaction {
                            Descriptions.update({ Descriptions.id eq id }) {
                                it[Descriptions.descriptionText] = request.descriptionText
                                it[Descriptions.updatedAt] = Instant.now()
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Description updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Description not found")
                        }
                    }

                    delete("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@delete
                        }

                        val deleted = transaction {
                            Descriptions.deleteWhere { Descriptions.id eq id }
                        }

                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Description deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Description not found")
                        }
                    }
                }

                // CRUD категории
                route("/categories") {
                    post {
                        val request = call.receive<CategoryRequest>()
                        val newId = transaction {
                            Categories.insert {
                                it[Categories.categoryName] = request.categoryName
                            }[Categories.id]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val request = call.receive<CategoryRequest>()
                        val updated = transaction {
                            Categories.update({ Categories.id eq id }) {
                                it[Categories.categoryName] = request.categoryName
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Category updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Category not found")
                        }
                    }

                    delete("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@delete
                        }

                        val deleted = transaction {
                            Categories.deleteWhere { Categories.id eq id }
                        }

                        if (deleted > 0) {
                            call.respond(HttpStatusCode.OK, "Category deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Category not found")
                        }
                    }
                }

                // CRUD footer
                route("/footer") {
                    post {
                        val request = call.receive<FooterRequest>()
                        val newId = transaction {
                            Footer.insert {
                                it[Footer.companyName] = request.companyName
                                it[Footer.placementOfSelling] = request.placementOfSelling
                                it[Footer.companyPhone] = request.companyPhone
                                it[Footer.companyEmail] = request.companyEmail
                                it[Footer.socialLinks] = request.socialLinks
                            }[Footer.id]
                        }
                        call.respond(HttpStatusCode.Created, mapOf("id" to newId))
                    }

                    put("/{id}") {
                        val id = call.parameters["id"]?.toIntOrNull()
                        if (id == null) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid ID")
                            return@put
                        }

                        val request = call.receive<FooterRequest>()
                        val updated = transaction {
                            Footer.update({ Footer.id eq id }) {
                                it[Footer.companyName] = request.companyName
                                it[Footer.placementOfSelling] = request.placementOfSelling
                                it[Footer.companyPhone] = request.companyPhone
                                it[Footer.companyEmail] = request.companyEmail
                                it[Footer.socialLinks] = request.socialLinks
                            }
                        }

                        if (updated > 0) {
                            call.respond(HttpStatusCode.OK, "Footer updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Footer not found")
                        }
                    }
                }
            }
        }

        // ADMIN эндпоинты
        authenticate("admin-jwt") {
            route("/admin/content-managers") {
                get {
                    val managers = transaction {
                        Users.selectAll().where { Users.role eq UserRole.CONTENT_MANAGER.name }
                            .map { row ->
                                mapOf(
                                    "id" to row[Users.id].toString(),
                                    "name" to row[Users.name],
                                    "surname" to row[Users.surname],
                                    "patronymic" to row[Users.patronymic],
                                    "email" to row[Users.email],
                                    "phone" to row[Users.phone]
                                )
                            }
                    }
                    call.respond(managers)
                }

                post {
                    val request = call.receive<ContentManagerRequest>()
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
                                it[Users.createdAt] = Instant.now()
                                it[Users.updatedAt] = Instant.now()
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

                put("/{id}") {
                    val idString = call.parameters["id"]
                    val id = try {
                        UUID.fromString(idString)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid UUID")
                        return@put
                    }

                    val request = call.receive<ContentManagerRequest>()
                    val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())

                    val updated = transaction {
                        Users.update({ (Users.id eq id) and (Users.role eq UserRole.CONTENT_MANAGER.name) }) {
                            it[Users.name] = request.name
                            it[Users.surname] = request.surname
                            it[Users.patronymic] = request.patronymic
                            it[Users.email] = request.email
                            it[Users.phone] = request.phone
                            it[Users.password] = hashedPassword
                            it[Users.updatedAt] = Instant.now()
                        }
                    }

                    if (updated > 0) {
                        call.respond(HttpStatusCode.OK, "Content manager updated")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Content manager not found")
                    }
                }

                delete("/{id}") {
                    val idString = call.parameters["id"]
                    val id = try {
                        UUID.fromString(idString)
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.BadRequest, "Invalid UUID")
                        return@delete
                    }

                    val deleted = transaction {
                        Users.deleteWhere { (Users.id eq id) and (Users.role eq UserRole.CONTENT_MANAGER.name) }
                    }

                    if (deleted > 0) {
                        call.respond(HttpStatusCode.OK, "Content manager deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Content manager not found")
                    }
                }
            }
        }

        post("/admin/setup") {
            val adminName = "Admin"
            val adminSurname = "User"
            val adminEmail = "admin@example.com"
            val adminPassword = "admin123"
            val hashedPassword = BCrypt.hashpw(adminPassword, BCrypt.gensalt())

            try {
                val adminId = transaction {
                    Users.insert {
                        it[Users.name] = adminName
                        it[Users.surname] = adminSurname
                        it[Users.email] = adminEmail
                        it[Users.password] = hashedPassword
                        it[Users.role] = UserRole.ADMIN.name
                        it[Users.createdAt] = Instant.now()
                        it[Users.updatedAt] = Instant.now()
                    }[Users.id]
                }
                call.respond(
                    HttpStatusCode.Created,
                    mapOf("message" to "Initial admin setup completed", "id" to adminId.toString())
                )
            } catch (e: Exception) {
                call.respond(HttpStatusCode.Conflict, "Admin already exists")
            }
        }
    }
}