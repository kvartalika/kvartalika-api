package com.example.tables

import com.example.models.*
import kvartalica.Flats
import kvartalica.Homes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.math.BigDecimal

class FlatDAO {

    fun getAllFlats(): List<Flat> = transaction {
        Flats.selectAll().map { toFlat(it) }
    }

    fun getFlatById(id: Int): Flat? = transaction {
        Flats.selectAll().where { Flats.id eq id }
            .mapNotNull { toFlat(it) }
            .singleOrNull()
    }

    fun getFlatsByHome(homeId: Int): List<Flat> = transaction {
        Flats.selectAll().where { Flats.homeId eq homeId }
            .map { toFlat(it) }
    }

    fun searchFlats(query: FlatSearchQuery): List<Flat> = transaction {
        var selectQuery = Flats.selectAll()

        query.minPrice?.let { minPrice ->
            minPrice.toBigDecimalOrNull()?.let { price ->
                selectQuery = selectQuery.andWhere { Flats.flatValue greaterEq price }
            }
        }

        query.maxPrice?.let { maxPrice ->
            maxPrice.toBigDecimalOrNull()?.let { price ->
                selectQuery = selectQuery.andWhere { Flats.flatValue lessEq price }
            }
        }

        query.minRooms?.let { minRooms ->
            selectQuery = selectQuery.andWhere { Flats.amountOfRooms greaterEq minRooms }
        }

        query.maxRooms?.let { maxRooms ->
            selectQuery = selectQuery.andWhere { Flats.amountOfRooms lessEq maxRooms }
        }

        query.isDecorated?.let { decorated ->
            selectQuery = selectQuery.andWhere { Flats.isDecorated eq decorated }
        }

        query.homeId?.let { homeId ->
            selectQuery = selectQuery.andWhere { Flats.homeId eq homeId }
        }

        selectQuery.map { toFlat(it) }
    }

    fun getFlatWithDetails(id: Int): FlatWithDetails? = transaction {
        val flat = getFlatById(id) ?: return@transaction null

        val home = Homes.select { Homes.id eq flat.homeId }
            .mapNotNull { homeRow ->
                Home(
                    homeId = homeRow[Homes.id].value,
                    homeAddress = homeRow[Homes.homeAddress],
                    yearBuilt = homeRow[Homes.yearBuilt],
                    totalFloors = homeRow[Homes.totalFloors],
                    ownerId = homeRow[Homes.ownerId],
                    categoryId = homeRow[Homes.categoryId]?.value
                )
            }
            .singleOrNull()

        FlatWithDetails(
            flatId = flat.flatId,
            flatName = flat.flatName,
            flatValue = flat.flatValue,
            amountOfRooms = flat.amountOfRooms,
            flatFloor = flat.flatFloor,
            placementOfFlat = flat.placementOfFlat,
            amountOfBathrooms = flat.amountOfBathrooms,
            isDecorated = flat.isDecorated,
            home = home,
            descriptions = emptyList(), // TODO: implement when descriptions are linked
            photos = emptyList() // TODO: implement when photos are linked
        )
    }

    fun createFlat(request: CreateFlatRequest): Flat = transaction {
        if (!request.validateRooms()) {
            throw ValidationException("Amount of rooms must be greater than 0")
        }

        val flatId = Flats.insertAndGetId {
            it[flatName] = request.flatName
            it[flatValue] = request.flatValue?.toBigDecimalOrNull()
            it[amountOfRooms] = request.amountOfRooms
            it[flatFloor] = request.flatFloor
            it[placementOfFlat] = request.placementOfFlat
            it[amountOfBathrooms] = request.amountOfBathrooms
            it[isDecorated] = request.isDecorated
            it[homeId] = request.homeId
        }

        Flat(
            flatId = flatId.value,
            flatName = request.flatName,
            flatValue = request.flatValue,
            amountOfRooms = request.amountOfRooms,
            flatFloor = request.flatFloor,
            placementOfFlat = request.placementOfFlat,
            amountOfBathrooms = request.amountOfBathrooms,
            isDecorated = request.isDecorated,
            homeId = request.homeId
        )
    }

    fun updateFlat(id: Int, request: UpdateFlatRequest): Flat? = transaction {
        if (!request.validateRooms()) {
            throw ValidationException("Amount of rooms must be greater than 0")
        }

        val updateCount = Flats.update({ Flats.id eq id }) {
            request.flatName?.let { value -> it[flatName] = value }
            request.flatValue?.let { value -> it[flatValue] = value.toBigDecimalOrNull() }
            request.amountOfRooms?.let { value -> it[amountOfRooms] = value }
            request.flatFloor?.let { value -> it[flatFloor] = value }
            request.placementOfFlat?.let { value -> it[placementOfFlat] = value }
            request.amountOfBathrooms?.let { value -> it[amountOfBathrooms] = value }
            request.isDecorated?.let { value -> it[isDecorated] = value }
            request.homeId?.let { value -> it[homeId] = value }
        }

        if (updateCount > 0) getFlatById(id) else null
    }

    fun deleteFlat(id: Int): Boolean = transaction {
        Flats.deleteWhere { Flats.id eq id } > 0
    }

    fun getFlatsWithPagination(page: Int, pageSize: Int): Pair<List<Flat>, Int> = transaction {
        val offset = (page - 1) * pageSize
        val flats = Flats.selectAll()
            .limit(pageSize, offset.toLong())
            .map { toFlat(it) }

        val totalCount = Flats.selectAll().count().toInt()

        Pair(flats, totalCount)
    }

    private fun toFlat(row: ResultRow): Flat = Flat(
        flatId = row[Flats.id].value,
        flatName = row[Flats.flatName],
        flatValue = row[Flats.flatValue]?.toString(),
        amountOfRooms = row[Flats.amountOfRooms],
        flatFloor = row[Flats.flatFloor],
        placementOfFlat = row[Flats.placementOfFlat],
        amountOfBathrooms = row[Flats.amountOfBathrooms],
        isDecorated = row[Flats.isDecorated],
        homeId = row[Flats.homeId].value
    )
}