package com.kvartalica.dto

import kotlinx.serialization.Serializable

@Serializable
data class FlatCategoryDto(
    val flat: FlatDto,
    val categories: List<CategoryDto> = emptyList(),
)