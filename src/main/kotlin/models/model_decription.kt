package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Description(
    val descriptionId: Int,
    val descriptionText: String? = null
)

@Serializable
data class CreateDescriptionRequest(
    val descriptionText: String
)

@Serializable
data class UpdateDescriptionRequest(
    val descriptionText: String
)