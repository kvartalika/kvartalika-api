package com.kvartalica.routes

import com.kvartalica.dto.PageInfoDto
import com.kvartalica.dto.SearchRequest
import com.kvartalica.dto.SocialMediaDto
import com.kvartalica.models.Categories
import com.kvartalica.models.Flats
import com.kvartalica.models.PageInfo
import com.kvartalica.models.Homes
import com.kvartalica.repository.PageRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.Base64

fun Route.clientRoutes() {
//    get("/categories") {
//        val categories = transaction {
//            Categories.selectAll().map { row ->
//                mapOf(
//                    "id" to row[Categories.id],
//                    "categoryName" to row[Categories.categoryName]
//                )
//            }
//        }
//        call.respond(categories)
//    }
//
//    get("/homes") {
//        val homes = transaction {
//            Homes.selectAll().map { row ->
//                mapOf(
//                    "id" to row[Homes.id],
//                    "categoryId" to row[Homes.categoryId],
//                    "homeAddress" to row[Homes.homeAddress],
//                    "yearBuilt" to row[Homes.yearBuilt],
//                    "totalFloors" to row[Homes.totalFloors],
//                    "ownerId" to row[Homes.ownerId]
//                )
//            }
//        }
//        call.respond(homes)
//    }
//
//    get("/flats") {
//        val flats = transaction {
//            Flats.selectAll().map { row ->
//                mapOf(
//                    "flatId" to row[Flats.flatId],
//                    "homeId" to row[Flats.homeId],
//                    "flatName" to row[Flats.flatName],
//                    "flatValue" to row[Flats.flatValue],
//                    "amountOfRooms" to row[Flats.amountOfRooms],
//                    "flatFloor" to row[Flats.flatFloor],
//                    "placementOfFlat" to row[Flats.placementOfFlat],
//                    "amountOfBathrooms" to row[Flats.amountOfBathrooms],
//                    "isDecorated" to row[Flats.isDecorated],
//                    "area" to row[Flats.area]
//                )
//            }
//        }
//        call.respond(flats)
//    }
//
//    post("/search") {
//        val request = call.receive<SearchRequest>()
//        val homes = transaction {
//            val query = Homes.selectAll()
//
//            request.categoryId?.let { categoryId ->
//                query.andWhere { Homes.categoryId eq categoryId }
//            }
//
//            query.map { row ->
//                mapOf(
//                    "id" to row[Homes.id],
//                    "categoryId" to row[Homes.categoryId],
//                    "homeAddress" to row[Homes.homeAddress],
//                    "yearBuilt" to row[Homes.yearBuilt],
//                    "totalFloors" to row[Homes.totalFloors]
//                )
//            }
//        }
//        call.respond(homes)
//    }

    get("/images/{directory}/{id}") {

    }

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
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, "Failed to get SocialMedia: ${e.message}")
        }
    }
}