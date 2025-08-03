package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(
    val query: String? = null,
    val minPrice: Long? = null,
    val maxPrice: Long? = null,
    val rooms: Int? = null,
    val bathrooms: Int? = null,
    val isDecorated: Boolean? = null,
    val homeId: Int? = null,
    val hasParks: Boolean? = null,
    val hasSchools: Boolean? = null,
    val hasStores: Boolean? = null,
    val categoriesId: List<Int>? = null,
    val sortBy: String? = null, // 'price' | 'rooms' | 'area' | 'location'
    val sortOrder: String? = null, // 'asc' | 'desc'
)