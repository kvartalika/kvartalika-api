package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlatRequest(
    val homeId: Int,
    val flatName: String? = null,
    val flatValue: Double? = null,
    val amountOfRooms: Int? = null,
    val flatFloor: Int? = null,
    val placementOfFlat: String? = null,
    val amountOfBathrooms: Int = 1,
    val isDecorated: Boolean = false,
    val area: String? = null
)
