package com.kvartalica

import com.kvartalica.config.configureDatabase
import com.kvartalica.config.configureRouting
import com.kvartalica.config.configureSecurity
import com.kvartalica.utils.configureHTTP
import com.kvartalica.utils.configureMonitoring
import com.kvartalica.utils.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureHTTP()
    configureSecurity()
    configureSerialization()
    configureDatabase()
    configureMonitoring()
    configureRouting()
}

