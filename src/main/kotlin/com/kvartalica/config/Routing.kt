package com.kvartalica.config

import com.kvartalica.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    routing {
        contentManagerRoutes()
        adminRoutes()
        clientRoutes()

        filesRoutes()
        pageRoutes()
        bidsRoutes()
        categoryRoutes()
        flatRoutes()
        homeRoutes()
    }
}