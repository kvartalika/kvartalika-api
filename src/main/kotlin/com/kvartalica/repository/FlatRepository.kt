package com.kvartalica.repository

import com.kvartalica.dto.CategoryDto
import com.kvartalica.dto.FlatCategoryDto
import com.kvartalica.dto.FlatDto
import com.kvartalica.models.Categories
import com.kvartalica.models.FlatCategories
import com.kvartalica.models.Flats
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object FlatRepository {
    fun getById(id: Int): FlatDto? = transaction {
        Flats.select { Flats.id eq id }
            .map { row ->
                FlatDto(
                    id = row[Flats.id].value,
                    name = row[Flats.name],
                    description = row[Flats.description],
                    images = row[Flats.images],
                    layout = row[Flats.layout],
                    address = row[Flats.address],
                    price = row[Flats.price],
                    latitude = row[Flats.latitude]?.toDouble(),
                    longitude = row[Flats.longitude]?.toDouble(),
                    features = row[Flats.features],
                    numberOfRooms = row[Flats.numberOfRooms],
                    area = row[Flats.area]?.toDouble(),
                    about = row[Flats.about],
                    floor = row[Flats.floor],
                    categoryId = row[Flats.categoryId],
                    homeId = row[Flats.homeId],
                    numberOfBathrooms = row[Flats.numberOfBathrooms],
                    hasDecoration = row[Flats.hasDecoration],
                    numberForSale = row[Flats.numberForSale],
                    published = row[Flats.published]
                )
            }.singleOrNull()
    }

    fun getAll(): List<FlatDto> = transaction {
        Flats.selectAll().map { row ->
            FlatDto(
                id = row[Flats.id].value,
                name = row[Flats.name],
                description = row[Flats.description],
                images = row[Flats.images],
                layout = row[Flats.layout],
                address = row[Flats.address],
                price = row[Flats.price],
                latitude = row[Flats.latitude]?.toDouble(),
                longitude = row[Flats.longitude]?.toDouble(),
                features = row[Flats.features],
                numberOfRooms = row[Flats.numberOfRooms],
                area = row[Flats.area]?.toDouble(),
                about = row[Flats.about],
                floor = row[Flats.floor],
                categoryId = row[Flats.categoryId],
                homeId = row[Flats.homeId],
                numberOfBathrooms = row[Flats.numberOfBathrooms],
                hasDecoration = row[Flats.hasDecoration],
                numberForSale = row[Flats.numberForSale],
                published = row[Flats.published]
            )
        }
    }

    fun create(flatDto: FlatDto) {
        transaction {
            Flats.insert {
                it[Flats.name] = flatDto.name
                it[Flats.description] = flatDto.description
                it[Flats.images] = flatDto.images
                it[Flats.layout] = flatDto.layout
                it[Flats.address] = flatDto.address
                it[Flats.price] = flatDto.price
                it[Flats.latitude] = flatDto.latitude?.toBigDecimal()
                it[Flats.longitude] = flatDto.longitude?.toBigDecimal()
                it[Flats.features] = flatDto.features
                it[Flats.numberOfRooms] = flatDto.numberOfRooms
                it[Flats.area] = flatDto.area?.toBigDecimal()
                it[Flats.about] = flatDto.about
                it[Flats.floor] = flatDto.floor
                it[Flats.categoryId] = flatDto.categoryId
                it[Flats.homeId] = flatDto.homeId
                it[Flats.numberOfBathrooms] = flatDto.numberOfBathrooms
                it[Flats.hasDecoration] = flatDto.hasDecoration
                it[Flats.numberForSale] = flatDto.numberForSale
                it[Flats.published] = flatDto.published
            }
        }
    }

    fun update(id: Int, flatDto: FlatDto) {
        transaction {
            Flats.update({ Flats.id eq id }) {
                it[name] = flatDto.name
                it[description] = flatDto.description
                it[images] = flatDto.images
                it[layout] = flatDto.layout
                it[address] = flatDto.address
                it[price] = flatDto.price
                it[latitude] = flatDto.latitude?.toBigDecimal()
                it[longitude] = flatDto.longitude?.toBigDecimal()
                it[features] = flatDto.features
                it[numberOfRooms] = flatDto.numberOfRooms
                it[area] = flatDto.area?.toBigDecimal()
                it[about] = flatDto.about
                it[floor] = flatDto.floor
                it[categoryId] = flatDto.categoryId
                it[homeId] = flatDto.homeId
                it[numberOfBathrooms] = flatDto.numberOfBathrooms
                it[hasDecoration] = flatDto.hasDecoration
                it[numberForSale] = flatDto.numberForSale
                it[published] = flatDto.published
            }
        }
    }

    fun deleteFlat(id: Int) {
        transaction {
            FlatCategories.deleteWhere { FlatCategories.flat eq id }
            Flats.deleteWhere { Flats.id eq id }
        }
    }

    fun getFlatsByHome(hId: Int): List<FlatCategoryDto> = transaction {
        (Flats leftJoin FlatCategories leftJoin Categories)
            .select { Flats.homeId eq hId }
            .map { row ->
                val flatDto = FlatDto(
                    id = row[Flats.id].value,
                    name = row[Flats.name],
                    description = row[Flats.description],
                    images = row[Flats.images],
                    layout = row[Flats.layout],
                    address = row[Flats.address],
                    price = row[Flats.price],
                    latitude = row[Flats.latitude]?.toDouble(),
                    longitude = row[Flats.longitude]?.toDouble(),
                    features = row[Flats.features],
                    numberOfRooms = row[Flats.numberOfRooms],
                    area = row[Flats.area]?.toDouble(),
                    about = row[Flats.about],
                    floor = row[Flats.floor],
                    categoryId = row[Flats.categoryId],
                    homeId = row[Flats.homeId],
                    numberOfBathrooms = row[Flats.numberOfBathrooms],
                    hasDecoration = row[Flats.hasDecoration],
                    numberForSale = row[Flats.numberForSale],
                    published = row[Flats.published]
                )
                val categoryDto: CategoryDto? = row.getOrNull(FlatCategories.categories)?.let { catId ->
                    CategoryDto(
                        id = row[Categories.id].value,
                        name = row[Categories.name],
                        isOnMainPage = row[Categories.isOnMainPage]
                    )
                }
                FlatCategoryDto(flat = flatDto, category = categoryDto)
            }
    }

    fun getFlatsByCategory(cId: Int): List<FlatCategoryDto> = transaction {
        (Flats innerJoin FlatCategories innerJoin Categories)
            .select { Categories.id eq cId }
            .map { row ->
                val flatDto = FlatDto(
                    id = row[Flats.id].value,
                    name = row[Flats.name],
                    description = row[Flats.description],
                    images = row[Flats.images],
                    layout = row[Flats.layout],
                    address = row[Flats.address],
                    price = row[Flats.price],
                    latitude = row[Flats.latitude]?.toDouble(),
                    longitude = row[Flats.longitude]?.toDouble(),
                    features = row[Flats.features],
                    numberOfRooms = row[Flats.numberOfRooms],
                    area = row[Flats.area]?.toDouble(),
                    about = row[Flats.about],
                    floor = row[Flats.floor],
                    categoryId = row[Flats.categoryId],
                    homeId = row[Flats.homeId],
                    numberOfBathrooms = row[Flats.numberOfBathrooms],
                    hasDecoration = row[Flats.hasDecoration],
                    numberForSale = row[Flats.numberForSale],
                    published = row[Flats.published]
                )
                val categoryDto = CategoryDto(
                    id = row[Categories.id].value,
                    name = row[Categories.name],
                    isOnMainPage = row[Categories.isOnMainPage]
                )
                FlatCategoryDto(flat = flatDto, category = categoryDto)
            }
    }
}