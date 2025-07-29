package com.kvartalica.models

import org.jetbrains.exposed.sql.Table

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