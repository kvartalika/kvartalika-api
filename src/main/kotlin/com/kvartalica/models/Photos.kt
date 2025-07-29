package com.kvartalica.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Photos : Table("public.photos") {
    val id = integer("id").autoIncrement()
    val photo = binary("image")  // для BYTEA в PostgreSQL
    val altText = varchar("alt_text", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}
