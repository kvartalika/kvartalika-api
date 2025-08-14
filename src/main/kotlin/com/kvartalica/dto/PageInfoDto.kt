package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageInfoDto(
    val phone: String? = null,
    val email: String? = null,
    val footerDescription: String? = null,
    val title: String? = null,
    val address: String? = null,
    val description: String? = null,
    val published: Boolean = false,
    val privacy: String? = null,
)