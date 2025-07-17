package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val errors: List<String>? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                message = message,
                data = data
            )
        }

        fun <T> error(message: String, errors: List<String>? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                message = message,
                errors = errors
            )
        }
    }
}

@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
) {
    companion object {
        fun <T> create(
            items: List<T>,
            totalCount: Int,
            page: Int,
            pageSize: Int
        ): PaginatedResponse<T> {
            val totalPages = if (totalCount == 0) 0 else (totalCount + pageSize - 1) / pageSize
            return PaginatedResponse(
                items = items,
                totalCount = totalCount,
                page = page,
                pageSize = pageSize,
                totalPages = totalPages
            )
        }
    }
}

@Serializable
data class PaginationQuery(
    val page: Int = 1,
    val pageSize: Int = 20
) {
    val offset: Int get() = (page - 1) * pageSize

    fun validate(): Boolean {
        return page > 0 && pageSize > 0 && pageSize <= 100
    }
}