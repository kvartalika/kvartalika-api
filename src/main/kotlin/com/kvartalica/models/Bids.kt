package com.kvartalica.models

import org.jetbrains.exposed.sql.Table

object Bids : Table("public.bids") {
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val patronymic = varchar("patronymic", 100)
    val email = varchar("email", 100)
    val phone = varchar("phone", 100)
}