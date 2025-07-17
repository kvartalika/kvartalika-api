package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val categoryId: Int,
    val categoryName: String
)

@Serializable
data class CreateCategoryRequest(
    val categoryName: String
)

@Serializable
data class UpdateCategoryRequest(
    val categoryName: String
)