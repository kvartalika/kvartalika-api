package com.kvartalica.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.kvartalica.models.UserRole
import io.github.cdimascio.dotenv.dotenv
import java.util.*

object JwtConfig {
    val env = dotenv()

    private val SECRET = env["SECRET"]
    private val ISSUER = env["ISSUER"]
    private val VALIDITY_IN_MS = env["VALIDITY_IN_MS"].toLongOrNull() ?: 36_000_000

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