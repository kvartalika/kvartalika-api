package com.kvartalica.models

import org.jetbrains.exposed.sql.Table

object Footer : Table() {
    val id = integer("id").autoIncrement()
    val companyName = varchar("company_name", 200)
    val placementOfSelling = varchar("placement_of_selling", 255).nullable()
    val companyPhone = varchar("company_phone", 20).nullable()
    val companyEmail = varchar("company_email", 100).nullable()
    val socialLinks = text("social_links").nullable()
    override val primaryKey = PrimaryKey(id)
}