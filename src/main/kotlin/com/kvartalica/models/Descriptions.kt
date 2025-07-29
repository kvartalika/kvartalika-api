package com.kvartalica.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Descriptions : Table("public.descriptions") {
    val id = integer("id").autoIncrement()
    val descriptionText = text("description_text")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}