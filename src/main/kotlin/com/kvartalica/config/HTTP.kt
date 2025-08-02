package com.kvartalica.config

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.conditionalheaders.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    install(CORS) {
        anyHost()
        allowHost(Config.allowHostHttp, schemes = listOf("http"))
        allowHost(Config.allowHostHttps, schemes = listOf("https"))

        allowCredentials = true
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("MyCustomHeader")
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor")
    }
    install(ConditionalHeaders)
    install(Compression)

    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}