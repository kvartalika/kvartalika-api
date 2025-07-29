package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class RequestCreate(
    val name: String,
    val surname: String,
    val email: String,
    val phone: String
)
