package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeRequest(
    val categoryId: Int,
    val homeAddress: String? = null,
    val yearBuilt: Int? = null,
    val totalFloors: Int? = null,
    val ownerId: String? = null // Изменено на String для UUID
)
