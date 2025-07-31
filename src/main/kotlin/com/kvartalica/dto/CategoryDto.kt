package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Int = 0,
    val name: String,
    val isOnMainPage: Boolean = false,
)