package com.kvartalica.repository

import com.kvartalica.dto.HomeDto
import com.kvartalica.models.Homes
import com.kvartalica.utils.StringConvertor
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object HomeRepository {
    fun create(homeDto: HomeDto) {
        transaction {
            Homes.insert {
                it[name] = homeDto.name
                it[description] = homeDto.description
                it[image] = StringConvertor.joinToString(homeDto.images)
                it[address] = homeDto.address
                it[latitude] = homeDto.latitude?.toBigDecimal()
                it[longitude] = homeDto.longitude?.toBigDecimal()
                it[yearBuilt] = homeDto.yearBuilt
                it[history] = StringConvertor.joinToString(homeDto.history)
                it[historyImages] = StringConvertor.joinToString(homeDto.historyImages)
                it[features] = StringConvertor.joinToString(homeDto.features)
                it[about] = homeDto.about
                it[numberOfFloors] = homeDto.numberOfFloors
                it[storesNearby] = homeDto.storesNearby
                it[schoolsNearby] = homeDto.schoolsNearby
                it[hospitalsNearby] = homeDto.hospitalsNearby
                it[hasYards] = homeDto.hasYards
                it[yardsImages] = StringConvertor.joinToString(homeDto.yardsImages)
                it[published] = homeDto.published
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
                it[image] = StringConvertor.joinToString(homeDto.images)
                it[address] = homeDto.address
                it[latitude] = homeDto.latitude?.toBigDecimal()
                it[longitude] = homeDto.longitude?.toBigDecimal()
                it[yearBuilt] = homeDto.yearBuilt
                it[history] = StringConvertor.joinToString(homeDto.history)
                it[historyImages] = StringConvertor.joinToString(homeDto.historyImages)
                it[features] = StringConvertor.joinToString(homeDto.features)
                it[about] = homeDto.about
                it[numberOfFloors] = homeDto.numberOfFloors
                it[storesNearby] = homeDto.storesNearby
                it[schoolsNearby] = homeDto.schoolsNearby
                it[hospitalsNearby] = homeDto.hospitalsNearby
                it[hasYards] = homeDto.hasYards
                it[yardsImages] = StringConvertor.joinToString(homeDto.yardsImages)
                it[published] = homeDto.published
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
        images = StringConvertor.parseString(this[Homes.image]),
        address = this[Homes.address],
        latitude = this[Homes.latitude]?.toDouble(),
        longitude = this[Homes.longitude]?.toDouble(),
        yearBuilt = this[Homes.yearBuilt],
        history = StringConvertor.parseString(this[Homes.history]),
        historyImages = StringConvertor.parseString(this[Homes.historyImages]),
        features = StringConvertor.parseString(this[Homes.features]),
        about = this[Homes.about],
        numberOfFloors = this[Homes.numberOfFloors],
        storesNearby = this[Homes.storesNearby],
        schoolsNearby = this[Homes.schoolsNearby],
        hospitalsNearby = this[Homes.hospitalsNearby],
        hasYards = this[Homes.hasYards],
        yardsImages = StringConvertor.parseString(this[Homes.yardsImages]),
        published = this[Homes.published],
    )
}