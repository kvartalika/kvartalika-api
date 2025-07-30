package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class SocialMediaDto(
    val id: Int = 0,
    val image: String? = null,
    val link: String? = null,
)
