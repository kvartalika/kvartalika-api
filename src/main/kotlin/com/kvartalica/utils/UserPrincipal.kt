package com.kvartalica.utils

import com.kvartalica.models.UserRole
import io.ktor.server.auth.*

data class UserPrincipal(
    val userId: String,
    val username: String,
    val role: UserRole
) : Principal
