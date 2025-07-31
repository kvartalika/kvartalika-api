package com.kvartalica.repository

import com.kvartalica.dto.HomeDto
import com.kvartalica.models.Homes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object HomeRepository {
    fun create(homeDto: HomeDto) {
        transaction {
            Homes.insert {
                it[name] = homeDto.name
                it[description] = homeDto.description
                it[image] = homeDto.image
                it[address] = homeDto.address
                it[latitude] = homeDto.latitude?.toBigDecimal()
                it[longitude] = homeDto.longitude?.toBigDecimal()
                it[yearBuilt] = homeDto.yearBuilt
                it[history] = homeDto.history
                it[historyImages] = homeDto.historyImages
                it[features] = homeDto.features
                it[about] = homeDto.about
                it[numberOfFloors] = homeDto.numberOfFloors
                it[storesNearby] = homeDto.storesNearby
                it[schoolsNearby] = homeDto.schoolsNearby
                it[hospitalsNearby] = homeDto.hospitalsNearby
                it[hasYards] = homeDto.hasYards
                it[yardsImages] = homeDto.yardsImages
                it[published] = homeDto.published
                it[ownerId] = homeDto.ownerId
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun getAll(): List<HomeDto> = transaction {
        Homes.selectAll().map { row ->
            row.toHomeDto()
        }
    }

    fun getById(id: Int): HomeDto? = transaction {
        Homes.select { Homes.id eq id }
            .mapNotNull { it.toHomeDto() }
            .singleOrNull()
    }

    fun update(id: Int, homeDto: HomeDto) {
        transaction {
            Homes.update({ Homes.id eq id }) {
                it[name] = homeDto.name
                it[description] = homeDto.description
                it[image] = homeDto.image
                it[address] = homeDto.address
                it[latitude] = homeDto.latitude?.toBigDecimal()
                it[longitude] = homeDto.longitude?.toBigDecimal()
                it[yearBuilt] = homeDto.yearBuilt
                it[history] = homeDto.history
                it[historyImages] = homeDto.historyImages
                it[features] = homeDto.features
                it[about] = homeDto.about
                it[numberOfFloors] = homeDto.numberOfFloors
                it[storesNearby] = homeDto.storesNearby
                it[schoolsNearby] = homeDto.schoolsNearby
                it[hospitalsNearby] = homeDto.hospitalsNearby
                it[hasYards] = homeDto.hasYards
                it[yardsImages] = homeDto.yardsImages
                it[published] = homeDto.published
                it[ownerId] = homeDto.ownerId
                it[updatedAt] = LocalDateTime.now()
            }
        }
    }

    fun delete(id: Int) {
        transaction {
            Homes.deleteWhere { Homes.id eq id }
        }
    }

    private fun ResultRow.toHomeDto(): HomeDto = HomeDto(
        id = this[Homes.id].value,
        name = this[Homes.name],
        description = this[Homes.description],
        image = this[Homes.image],
        address = this[Homes.address],
        latitude = this[Homes.latitude]?.toDouble(),
        longitude = this[Homes.longitude]?.toDouble(),
        yearBuilt = this[Homes.yearBuilt],
        history = this[Homes.history],
        historyImages = this[Homes.historyImages],
        features = this[Homes.features],
        about = this[Homes.about],
        numberOfFloors = this[Homes.numberOfFloors],
        storesNearby = this[Homes.storesNearby],
        schoolsNearby = this[Homes.schoolsNearby],
        hospitalsNearby = this[Homes.hospitalsNearby],
        hasYards = this[Homes.hasYards],
        yardsImages = this[Homes.yardsImages],
        published = this[Homes.published],
        ownerId = this[Homes.ownerId],
        createdAt = this[Homes.createdAt]?.toString(),
        updatedAt = this[Homes.updatedAt]?.toString()
    )
}