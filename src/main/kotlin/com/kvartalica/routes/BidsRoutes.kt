package com.kvartalica.routes

import com.kvartalica.dto.BidsDto
import com.kvartalica.repository.BidsRepository
import com.kvartalica.utils.TelegramNotificationService
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bidsRoutes() {
    route("/bids") {
        post {
            val bid = call.receive<BidsDto>()
            BidsRepository.create(bid)
            TelegramNotificationService.notifyAllUsers(
                message = "Новая заявка на просмотр!\n\n${bid.email}\n${bid.surname} ${bid.name} ${bid.patronymic}"
            )
            call.respond(HttpStatusCode.Created)
        }
        authenticate("admin-jwt", "content-jwt") {
            get {
                val bids = BidsRepository.findAll()
                call.respond(bids)
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@get call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                BidsRepository.findById(id)
                    ?.let { call.respond(it) }
                    ?: call.respond(HttpStatusCode.NotFound, "Bid not found")
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@put call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                val bid = call.receive<BidsDto>()
                BidsRepository.update(id, bid)
                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) return@delete call.respond(HttpStatusCode.BadRequest, "Invalid or missing id")
                BidsRepository.delete(id)
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}