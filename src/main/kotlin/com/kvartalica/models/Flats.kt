package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Flats : IntIdTable() {
    val name = varchar("name", length = 255).nullable()
    val description = text("description").nullable()
    val images = text("images").nullable()
    val layout = varchar("layout", length = 255).nullable()
    val address = varchar("address", length = 500).nullable()
    val price = integer("price").nullable()
    val latitude = decimal("latitude", precision = 9, scale = 6).nullable()
    val longitude = decimal("longitude", precision = 9, scale = 6).nullable()
    val features = text("features").nullable()
    val numberOfRooms = integer("numberOfRooms").nullable()
    val area = decimal("area", precision = 10, scale = 2).nullable()
    val about = varchar("about", length = 1_000).nullable()
    val floor = integer("floor").nullable()
    val homeId = integer("homeId").nullable()
    val numberOfBathrooms = integer("numberOfBathrooms").nullable()
    val hasDecoration = bool("hasDecoration").default(false)
    val numberForSale = integer("numberForSale").nullable()
    val published = bool("published").default(false)
}