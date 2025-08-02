package com.kvartalica

import com.kvartalica.config.*
import com.kvartalica.utils.configureSerialization
import com.kvartalica.utils.initAdmin
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDatabase()
    configureSecurity()
    configureSerialization()
    configureHTTP()
    configureRouting()
    configureMonitoring()

    initAdmin()
}
