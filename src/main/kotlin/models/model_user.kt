package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class User(
    val userId: String, // UUID as String for serialization
    val username: String,
    val userSurname: String,
    val userPatronymic: String? = null,
    val userPhone: String? = null,
    val userEmail: String,
    val createdAt: String, // LocalDateTime as String for serialization
    val updatedAt: String
) {
    companion object {
        fun fromCreateRequest(request: CreateUserRequest): User {
            val now = LocalDateTime.now().toString()
            return User(
                userId = UUID.randomUUID().toString(),
                username = request.username,
                userSurname = request.userSurname,
                userPatronymic = request.userPatronymic,
                userPhone = request.userPhone,
                userEmail = request.userEmail,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}

@Serializable
data class CreateUserRequest(
    val username: String,
    val userSurname: String,
    val userPatronymic: String? = null,
    val userPhone: String? = null,
    val userEmail: String
)

@Serializable
data class UpdateUserRequest(
    val username: String? = null,
    val userSurname: String? = null,
    val userPatronymic: String? = null,
    val userPhone: String? = null,
    val userEmail: String? = null
)

@Serializable
data class UserResponse(
    val userId: String,
    val username: String,
    val userSurname: String,
    val userPatronymic: String? = null,
    val userPhone: String? = null,
    val userEmail: String,
    val createdAt: String
)