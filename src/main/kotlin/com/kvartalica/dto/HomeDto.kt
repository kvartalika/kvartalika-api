package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeDto(
    val id: Int = 0,
    val name: String? = null,
    val description: String? = null,
    val images: List<String>? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val yearBuilt: Int? = null,
    val history: List<String>? = null,
    val historyImages: List<String>? = null,
    val features: List<String>? = null,
    val about: String? = null,
    val numberOfFloors: Int? = null,
    val storesNearby: Boolean = false,
    val schoolsNearby: Boolean = false,
    val hospitalsNearby: Boolean = false,
    val hasYards: Boolean = false,
    val yardsImages: List<String>? = null,
    val published: Boolean = false,
)
