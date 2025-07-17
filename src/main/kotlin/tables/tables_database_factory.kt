package com.example.tables

import kvartalica.Categories
import kvartalica.Descriptions
import kvartalica.Flats
import kvartalica.Footer
import kvartalica.Homes
import kvartalica.Photos
import kvartalica.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/real_estate_db"
        val user = "your_username"
        val password = "your_password"

        val database = Database.connect(jdbcURL, driverClassName, user, password)

        transaction(database) {
            SchemaUtils.create(
                Users,
                Categories,
                Homes,
                Flats,
                Descriptions,
                Photos,
                Footer
            )
        }
    }

    fun initWithTestData() {
        init()

        transaction {
            // Insert test categories
            if (Categories.selectAll().empty()) {
                Categories.insert {
                    it[categoryName] = "Residential"
                }
                Categories.insert {
                    it[categoryName] = "Commercial"
                }
                Categories.insert {
                    it[categoryName] = "Luxury"
                }
            }

            // Insert test footer
            if (Footer.selectAll().empty()) {
                Footer.insert {
                    it[companyName] = "Premium Real Estate"
                    it[placementOfSelling] = "Downtown Business District"
                    it[companyPhone] = "+1-555-0123"
                    it[companyEmail] = "info@premiumrealestate.com"
                }
            }
        }
    }
}