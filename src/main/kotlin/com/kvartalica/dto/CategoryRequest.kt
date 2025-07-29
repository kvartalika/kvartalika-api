package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class CategoryRequest(
    val categoryName: String
)
