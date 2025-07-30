package com.kvartalica.models

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : UUIDTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val patronymic = varchar("patronymic", 50).nullable()
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 20).uniqueIndex().nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }
    val role = varchar("role", 50).default("CLIENT")
    val password = varchar("password", 100)
}