package com.kvartalica.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*

// Enum для ролей пользователей
enum class UserRole {
    CLIENT, ADMIN, CONTENT_MANAGER
}

// JWT конфигурация
object JwtConfig {
    private const val SECRET = "your-secret-key"
    private const val ISSUER = "kvartalica"
    private const val VALIDITY_IN_MS = 36_000_000 // 10 hours

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(userId: String, username: String, role: UserRole): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(userId)
            .withClaim("username", username)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJWT? {
        return try {
            JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
        } catch (e: Exception) {
            null
        }
    }

    val jwtVerifier = JWT.require(algorithm)
        .withIssuer(ISSUER)
        .build()
}

// Principal для пользователей
data class UserPrincipal(
    val userId: String,
    val username: String,
    val role: UserRole
) : Principal

// Конфигурация аутентификации
fun Application.configureSecurity() {
    install(Authentication) {
        // JWT для обычных пользователей и админов
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

        // JWT только для админов
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