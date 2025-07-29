package com.kvartalica.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Homes : Table() {
    val id = integer("id").autoIncrement()
    val categoryId = integer("category_id") references Categories.id
    val homeAddress = varchar("home_address", 255).nullable()
    val yearBuilt = integer("year_built").nullable()
    val totalFloors = integer("total_floors").nullable()
    val ownerId = integer("owner_id").nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}