package com.kvartalica.models

import org.jetbrains.exposed.sql.Table

object FlatCategories : Table() {
    val flat = reference("flat", Flats)
    val categories = reference("categories", Categories)
    override val primaryKey = PrimaryKey(flat, categories, name = "flat_categories_id")
}