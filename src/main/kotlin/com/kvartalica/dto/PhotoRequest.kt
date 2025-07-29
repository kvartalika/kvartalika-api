package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class PhotoRequest(val altText: String? = null)
