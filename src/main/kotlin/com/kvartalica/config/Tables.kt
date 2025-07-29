package com.kvartalica.config

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object Categories : Table() {
    val id = integer("category_id").autoIncrement()
    val categoryName = varchar("category_name", 255)
    override val primaryKey = PrimaryKey(id)
}

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

object Flats : Table() {
    val homeId = integer("home_id") references Homes.id
    val flatId = integer("flat_id").autoIncrement()
    val flatName = varchar("flat_name", 100).nullable()
    val flatValue = decimal("flat_value", 12, 2).nullable()
    val amountOfRooms = integer("amount_of_rooms").nullable()
    val flatFloor = integer("flat_floor").nullable()
    val placementOfFlat = varchar("placement_of_flat", 100).nullable()
    val amountOfBathrooms = integer("amount_of_bathrooms").default(1)
    val isDecorated = bool("is_decorated").default(false)
    val area = varchar("area", 255).nullable()
    override val primaryKey = PrimaryKey(flatId)
}

object Footer : Table() {
    val id = integer("id").autoIncrement()
    val companyName = varchar("company_name", 200)
    val placementOfSelling = varchar("placement_of_selling", 255).nullable()
    val companyPhone = varchar("company_phone", 20).nullable()
    val companyEmail = varchar("company_email", 100).nullable()
    val socialLinks = text("social_links").nullable()
    override val primaryKey = PrimaryKey(id)
}

object Users : UUIDTable() {
    val name = varchar("name", 50)
    val surname = varchar("surname", 50)
    val patronymic = varchar("patronymic", 50).nullable()
    val email = varchar("email", 255).uniqueIndex()
    val phone = varchar("phone", 20).uniqueIndex().nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    val role = varchar("role", 50).default("CLIENT")
    val password = varchar("password", 100)
}


object Photos : Table("public.photos") {
    val id = integer("id").autoIncrement()
    val photo = binary("image")  // для BYTEA в PostgreSQL
    val altText = varchar("alt_text", 255).nullable()
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object Descriptions : Table("public.descriptions") {
    val id = integer("id").autoIncrement()
    val descriptionText = text("description_text")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at")
    override val primaryKey = PrimaryKey(id)
}

object Bids : Table("public.bids") {
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val patronymic = varchar("patronymic", 100)
    val email = varchar("email", 100)
    val phone = varchar("phone", 100)
}
