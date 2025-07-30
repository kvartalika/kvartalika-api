package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Homes : IntIdTable() {
    val name = varchar("name", 255).nullable()
    val description = text("description").nullable()
    val image = varchar("image", 512).nullable()
    val address = varchar("address", 512).nullable()
    val latitude = decimal("latitude", 9, 6).nullable()
    val longitude = decimal("longitude", 9, 6).nullable()
    val yearBuilt = integer("yearBuilt").nullable()
    val history = text("history").nullable()
    val historyImages = text("historyImages").nullable()
    val features = text("features").nullable()
    val about = varchar("about", 512).nullable()
    val numberOfFloors = integer("numberOfFloors").nullable()
    val storesNearby = bool("storesNearby").default(false)
    val schoolsNearby = bool("schoolsNearby").default(false)
    val hospitalsNearby = bool("hospitalsNearby").default(false)
    val hasYards = bool("hasYards").default(false)
    val yardsImages = text("yardsImages").nullable()
    val published = bool("published").default(false)
    val ownerId = integer("owner_id").nullable()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }.nullable()
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }.nullable()
}