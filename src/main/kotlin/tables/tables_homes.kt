package com.example.tables

import com.example.models.*
import kvartalica.Categories
import kvartalica.Flats
import kvartalica.Homes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class HomeDAO {

    fun getAllHomes(): List<Home> = transaction {
        Homes.selectAll().map { toHome(it) }
    }

    fun getHomeById(id: Int): Home? = transaction {
        Homes.selectAll().where { Homes.id eq id }
            .mapNotNull { toHome(it) }
            .singleOrNull()
    }

    fun getHomesByCategory(categoryId: Int): List<Home> = transaction {
        Homes.selectAll().where { Homes.categoryId eq categoryId }
            .map { toHome(it) }
    }

    fun getHomesByOwner(ownerId: Int): List<Home> = transaction {
        Homes.selectAll().where { Homes.ownerId eq ownerId }
            .map { toHome(it) }
    }

    fun getHomeWithDetails(id: Int): HomeWithDetails? = transaction {
        val home = getHomeById(id) ?: return@transaction null

        val category = home.categoryId?.let { categoryId ->
            Categories.select { Categories.id eq categoryId }
                .mapNotNull {
                    Category(
                        categoryId = it[Categories.id].value,
                        categoryName = it[Categories.categoryName]
                    )
                }
                .singleOrNull()
        }

        val flats = Flats.select { Flats.homeId eq id }
            .map { flatRow ->
                Flat(
                    flatId = flatRow[Flats.id].value,
                    flatName = flatRow[Flats.flatName],
                    flatValue = flatRow[Flats.flatValue]?.toString(),
                    amountOfRooms = flatRow[Flats.amountOfRooms],
                    flatFloor = flatRow[Flats.flatFloor],
                    placementOfFlat = flatRow[Flats.placementOfFlat],
                    amountOfBathrooms = flatRow[Flats.amountOfBathrooms],
                    isDecorated = flatRow[Flats.isDecorated],
                    homeId = flatRow[Flats.homeId].value
                )
            }

        HomeWithDetails(
            homeId = home.homeId,
            homeAddress = home.homeAddress,
            yearBuilt = home.yearBuilt,
            totalFloors = home.totalFloors,
            ownerId = home.ownerId,
            category = category,
            flats = flats
        )
    }

    fun createHome(request: CreateHomeRequest): Home = transaction {
        val homeId = Homes.insertAndGetId {
            it[homeAddress] = request.homeAddress
            it[yearBuilt] = request.yearBuilt
            it[totalFloors] = request.totalFloors
            it[ownerId] = request.ownerId
            it[categoryId] = request.categoryId
        }

        Home(
            homeId = homeId.value,
            homeAddress = request.homeAddress,
            yearBuilt = request.yearBuilt,
            totalFloors = request.totalFloors,
            ownerId = request.ownerId,
            categoryId = request.categoryId
        )
    }

    fun updateHome(id: Int, request: UpdateHomeRequest): Home? = transaction {
        val updateCount = Homes.update({ Homes.id eq id }) {
            request.homeAddress?.let { value -> it[homeAddress] = value }
            request.yearBuilt?.let { value -> it[yearBuilt] = value }
            request.totalFloors?.let { value -> it[totalFloors] = value }
            request.ownerId?.let { value -> it[ownerId] = value }
            request.categoryId?.let { value -> it[categoryId] = value }
        }

        if (updateCount > 0) getHomeById(id) else null
    }

    fun deleteHome(id: Int): Boolean = transaction {
        Homes.deleteWhere { Homes.id eq id } > 0
    }

    private fun toHome(row: ResultRow): Home = Home(
        homeId = row[Homes.id].value,
        homeAddress = row[Homes.homeAddress],
        yearBuilt = row[Homes.yearBuilt],
        totalFloors = row[Homes.totalFloors],
        ownerId = row[Homes.ownerId],
        categoryId = row[Homes.categoryId]?.value
    )
}