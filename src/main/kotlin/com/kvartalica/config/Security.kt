package com.kvartalica.config

import com.kvartalica.models.UserRole
import com.kvartalica.utils.JwtConfig
import com.kvartalica.utils.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureSecurity() {
    install(Authentication) {
        jwt("content-jwt") {
            verifier(JwtConfig.jwtVerifier)
            validate { credential ->
                val userId = credential.payload.subject
                val username = credential.payload.getClaim("username").asString()
                val role = credential.payload.getClaim("role").asString()?.let {
                    UserRole.valueOf(it)
                }

                if (userId != null && username != null && role != null) {
                    UserPrincipal(userId, username, role)
                } else {
                    null
                }
            }
        }

        jwt("admin-jwt") {
            verifier(JwtConfig.jwtVerifier)
            validate { credential ->
                val userId = credential.payload.subject
                val username = credential.payload.getClaim("username").asString()
                val role = credential.payload.getClaim("role").asString()?.let {
                    UserRole.valueOf(it)
                }

                if (userId != null && username != null && role == UserRole.ADMIN) {
                    UserPrincipal(userId, username, role)
                } else {
                    null
                }
            }
        }
    }
}