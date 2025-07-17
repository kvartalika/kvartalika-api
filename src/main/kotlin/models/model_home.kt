package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val homeId: Int,
    val homeAddress: String? = null,
    val yearBuilt: Int? = null,
    val totalFloors: Int? = null,
    val ownerId: Int? = null,
    val categoryId: Int? = null
)

@Serializable
data class CreateHomeRequest(
    val homeAddress: String? = null,
    val yearBuilt: Int? = null,
    val totalFloors: Int? = null,
    val ownerId: Int? = null,
    val categoryId: Int? = null
)

@Serializable
data class UpdateHomeRequest(
    val homeAddress: String? = null,
    val yearBuilt: Int? = null,
    val totalFloors: Int? = null,
    val ownerId: Int? = null,
    val categoryId: Int? = null
)

@Serializable
data class HomeWithDetails(
    val homeId: Int,
    val homeAddress: String? = null,
    val yearBuilt: Int? = null,
    val totalFloors: Int? = null,
    val ownerId: Int? = null,
    val category: Category? = null,
    val flats: List<Flat> = emptyList()
)