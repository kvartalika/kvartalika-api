package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlatDto(
    val id: Int = 0,
    val name: String? = null,
    val description: String? = null,
    val images: String? = null,
    val layout: String? = null,
    val address: String? = null,
    val price: Int? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val features: String? = null,
    val numberOfRooms: Int? = null,
    val area: Double? = null,
    val about: String? = null,
    val floor: Int? = null,
    val categoryId: Int? = null,
    val homeId: Int? = null,
    val numberOfBathrooms: Int? = null,
    val hasDecoration: Boolean? = null,
    val numberForSale: Int? = null,
    val published: Boolean = false,
)
