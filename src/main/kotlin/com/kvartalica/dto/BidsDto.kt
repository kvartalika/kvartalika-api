package com.kvartalica.dto

import com.kvartalica.utils.TimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class BidsDto(
    val id: Int = 0,
    val name: String,
    val surname: String,
    val patronymic: String,
    val email: String,
    val phone: String,
    @Serializable(with = TimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isChecked: Boolean = false,
)