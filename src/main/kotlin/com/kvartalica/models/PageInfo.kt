package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable

object PageInfo : IntIdTable() {
    val phone = varchar("phone", 31).nullable()
    val email = varchar("email", 255).nullable()
    val footerDescription = text("footer").nullable()
    val title = text("title").nullable()
    val address = text("address").nullable()
    val description = text("description").nullable()
    val published = bool("published").default(false)
}