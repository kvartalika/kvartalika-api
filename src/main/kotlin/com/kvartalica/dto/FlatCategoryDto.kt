package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlatCategoryDto(
    val flat: FlatDto,
    val category: CategoryDto? = null,
)