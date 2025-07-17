package com.example.models

import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Flat(
    val flatId: Int,
    val flatName: String? = null,
    val flatValue: String? = null, // BigDecimal as String for serialization
    val amountOfRooms: Int? = null,
    val flatFloor: Int? = null,
    val placementOfFlat: String? = null,
    val amountOfBathrooms: Int = 1,
    val isDecorated: Boolean = false,
    val homeId: Int
)

@Serializable
data class CreateFlatRequest(
    val flatName: String? = null,
    val flatValue: String? = null,
    val amountOfRooms: Int? = null,
    val flatFloor: Int? = null,
    val placementOfFlat: String? = null,
    val amountOfBathrooms: Int = 1,
    val isDecorated: Boolean = false,
    val homeId: Int
) {
    fun validateRooms(): Boolean {
        return amountOfRooms == null || amountOfRooms > 0
    }
}

@Serializable
data class UpdateFlatRequest(
    val flatName: String? = null,
    val flatValue: String? = null,
    val amountOfRooms: Int? = null,
    val flatFloor: Int? = null,
    val placementOfFlat: String? = null,
    val amountOfBathrooms: Int? = null,
    val isDecorated: Boolean? = null,
    val homeId: Int? = null
) {
    fun validateRooms(): Boolean {
        return amountOfRooms == null || amountOfRooms > 0
    }
}

@Serializable
data class FlatWithDetails(
    val flatId: Int,
    val flatName: String? = null,
    val flatValue: String? = null,
    val amountOfRooms: Int? = null,
    val flatFloor: Int? = null,
    val placementOfFlat: String? = null,
    val amountOfBathrooms: Int = 1,
    val isDecorated: Boolean = false,
    val home: Home? = null,
    val descriptions: List<Description> = emptyList(),
    val photos: List<Photo> = emptyList()
)

@Serializable
data class FlatSearchQuery(
    val minPrice: String? = null,
    val maxPrice: String? = null,
    val minRooms: Int? = null,
    val maxRooms: Int? = null,
    val isDecorated: Boolean? = null,
    val homeId: Int? = null,
    val categoryId: Int? = null
)