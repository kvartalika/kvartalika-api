package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable

object SocialMedia : IntIdTable() {
    val image = varchar("image", 255).nullable()
    val link = varchar("link", 255).nullable()
}