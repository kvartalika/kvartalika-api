package com.kvartalica.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.kvartalica.config.Config
import com.kvartalica.models.UserRole
import java.util.*

object JwtConfig {
    private val SECRET = Config.secret
    private val ISSUER = Config.issuer
    private const val ACCESS_VALIDITY_MS = (15 * 60 * 1000)
    private const val REFRESH_VALIDITY_MS = (7 * 24 * 60 * 60 * 1000)

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateAccessToken(userId: String, username: String, role: UserRole): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(userId)
            .withClaim("username", username)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_VALIDITY_MS))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(userId)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_VALIDITY_MS))
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