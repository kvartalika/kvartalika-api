package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchRequest(
    val categoryId: Int? = null,
    val minValue: Double? = null,
    val maxValue: Double? = null,
    val minRooms: Int? = null,
    val maxRooms: Int? = null
)