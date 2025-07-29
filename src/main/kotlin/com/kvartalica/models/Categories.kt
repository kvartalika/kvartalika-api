package com.kvartalica.models

import org.jetbrains.exposed.sql.Table

object Categories : Table() {
    val id = integer("category_id").autoIncrement()
    val categoryName = varchar("category_name", 255)
    override val primaryKey = PrimaryKey(id)
}
