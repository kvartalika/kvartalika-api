package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Bids : IntIdTable() {
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val patronymic = varchar("patronymic", 100)
    val phone = varchar("phone", 100)
    val email = varchar("email", 100)
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val isChecked = bool("is_checked").default(false)
}