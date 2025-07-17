package com.example.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Footer(
    val footerId: Int,
    val companyName: String,
    val placementOfSelling: String? = null,
    val companyPhone: String? = null,
    val companyEmail: String? = null,
    val socialLinks: JsonObject? = null
)

@Serializable
data class CreateFooterRequest(
    val companyName: String,
    val placementOfSelling: String? = null,
    val companyPhone: String? = null,
    val companyEmail: String? = null,
    val socialLinks: JsonObject? = null
)

@Serializable
data class UpdateFooterRequest(
    val companyName: String? = null,
    val placementOfSelling: String? = null,
    val companyPhone: String? = null,
    val companyEmail: String? = null,
    val socialLinks: JsonObject? = null
)