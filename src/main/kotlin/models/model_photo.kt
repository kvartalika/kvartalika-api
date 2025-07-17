package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val photoId: Int,
    val photoUrl: String
)

@Serializable
data class CreatePhotoRequest(
    val photoUrl: String
)

@Serializable
data class UpdatePhotoRequest(
    val photoUrl: String
)