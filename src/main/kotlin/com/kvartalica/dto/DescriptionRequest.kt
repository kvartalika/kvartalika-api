package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class DescriptionRequest(
    val descriptionText: String
)
