package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class FooterRequest(
    val companyName: String,
    val placementOfSelling: String? = null,
    val companyPhone: String? = null,
    val companyEmail: String? = null,
    val socialLinks: String? = null
)
