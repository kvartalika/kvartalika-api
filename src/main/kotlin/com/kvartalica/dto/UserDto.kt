package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val name: String,
    val surname: String,
    val patronymic: String? = null,
    val email: String,
    val phone: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val role: String = "CLIENT",
    val password: String,
    val telegramId: String? = null,
)