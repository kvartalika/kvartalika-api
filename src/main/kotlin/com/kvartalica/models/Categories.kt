package com.kvartalica.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Categories : IntIdTable() {
    val name = varchar("name", 255)
    val isOnMainPage = bool("is_on_main_page").default(false)
}
