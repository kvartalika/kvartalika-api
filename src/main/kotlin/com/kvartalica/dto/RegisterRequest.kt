package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val name: String,
    val surname: String,
    val patronymic: String? = null,
    val email: String,
    val phone: String? = null,
    val password: String
)
