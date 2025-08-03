package com.kvartalica.repository

import com.kvartalica.dto.CategoryDto
import com.kvartalica.dto.FlatCategoryDto
import com.kvartalica.dto.FlatDto
import com.kvartalica.dto.SearchDto
import com.kvartalica.models.Categories
import com.kvartalica.models.FlatCategories
import com.kvartalica.models.Flats
import com.kvartalica.utils.StringConvertor
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.transactions.transaction

object FlatRepository {
    fun getById(id: Int): FlatCategoryDto? = transaction {
        val rows = (Flats leftJoin FlatCategories leftJoin Categories)
            .select { Flats.id eq id }
        if (rows.empty()) return@transaction null
        rows.groupBy(
            { it.toFlatDto() },
            { row -> row.getOrNull(Categories.id)?.let { row.toCategoryDto() } }
        ).entries.firstOrNull()?.let { (flat, cats) ->
            FlatCategoryDto(
                flat = flat,
                categories = cats.filterNotNull()
            )
        }
    }

    fun getAll(): List<FlatCategoryDto> = transaction {
        val rows = (Flats leftJoin FlatCategories leftJoin Categories)
            .selectAll()
        rows.groupBy(
            { it.toFlatDto() },
            { row -> row.getOrNull(Categories.id)?.let { row.toCategoryDto() } }
        ).map { (flat, cats) ->
            FlatCategoryDto(
                flat = flat,
                categories = cats.filterNotNull()
            )
        }
    }

    fun create(flatDto: FlatCategoryDto) {
        val flat = flatDto.flat
        val categories = flatDto.categories
        val cleanedImages = flat.images?.map { image ->
            if (image.startsWith("/")) image.removePrefix("/") else image
        }

        val cleanedLayout = if (flat.layout?.startsWith("/") == true) {
            flat.layout.removePrefix("/")
        } else {
            flat.layout
        }
        transaction {
            val insertedId = Flats.insertAndGetId {
                it[name] = flat.name
                it[description] = flat.description
                it[images] = StringConvertor.joinToString(cleanedImages)
                it[layout] = cleanedLayout
                it[address] = flat.address
                it[price] = flat.price
                it[latitude] = flat.latitude?.toBigDecimal()
                it[longitude] = flat.longitude?.toBigDecimal()
                it[features] = StringConvertor.joinToString(flat.features)
                it[numberOfRooms] = flat.numberOfRooms
                it[area] = flat.area?.toBigDecimal()
                it[about] = flat.about
                it[floor] = flat.floor
                it[homeId] = flat.homeId
                it[numberForSale] = flat.numberForSale
                it[numberOfBathrooms] = flat.numberOfBathrooms
                it[hasDecoration] = flat.hasDecoration
                it[published] = flat.published
            }.value

            categories.forEach { category ->
                CategoriesRepository.addCategoryToFlat(insertedId, category.id)
            }
        }
    }

    fun update(id: Int, flatDto: FlatCategoryDto) {
        val flat = flatDto.flat
        val categories = flatDto.categories

        val cleanedImages = flat.images?.map { image ->
            if (image.startsWith("/")) image.removePrefix("/") else image
        }

        val cleanedLayout = if (flat.layout?.startsWith("/") == true) {
            flat.layout.removePrefix("/")
        } else {
            flat.layout
        }

        transaction {
            Flats.update({ Flats.id eq id }) {
                it[name] = flat.name
                it[description] = flat.description
                it[images] = StringConvertor.joinToString(cleanedImages)
                it[layout] = cleanedLayout
                it[address] = flat.address
                it[price] = flat.price
                it[latitude] = flat.latitude?.toBigDecimal()
                it[longitude] = flat.longitude?.toBigDecimal()
                it[features] = StringConvertor.joinToString(flat.features)
                it[numberOfRooms] = flat.numberOfRooms
                it[area] = flat.area?.toBigDecimal()
                it[about] = flat.about
                it[floor] = flat.floor
                it[homeId] = flat.homeId
                it[numberForSale] = flat.numberForSale
                it[numberOfBathrooms] = flat.numberOfBathrooms
                it[hasDecoration] = flat.hasDecoration
                it[published] = flat.published
            }

            FlatCategories.deleteWhere { FlatCategories.flat eq id }
            categories.forEach { c ->
                CategoriesRepository.addCategoryToFlat(id, c.id)
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
        val flatIds = Flats
            .slice(Flats.id)
            .select { Flats.homeId eq hId }
            .map { it[Flats.id].value }

        if (flatIds.isEmpty()) return@transaction emptyList()

        val rows = (Flats leftJoin FlatCategories leftJoin Categories)
            .select { Flats.id inList flatIds }

        rows.groupBy(
            { it.toFlatDto() },
            { row -> row.getOrNull(Categories.id)?.let { row.toCategoryDto() } }
        ).map { (flat, categories) ->
            FlatCategoryDto(
                flat = flat,
                categories = categories.filterNotNull()
            )
        }
    }

    fun getFlatsByCategory(cId: Int): List<FlatCategoryDto> = transaction {
        val flatIds = FlatCategories.slice(FlatCategories.flat)
            .select { FlatCategories.categories eq cId }
            .map { it[FlatCategories.flat] }

        if (flatIds.isEmpty()) return@transaction emptyList()
        val rows = (Flats innerJoin FlatCategories innerJoin Categories)
            .select { Flats.id inList flatIds }

        rows.groupBy(
            { it.toFlatDto() },
            { row -> row.toCategoryDto() }
        ).map { (flat, categories) ->
            FlatCategoryDto(flat = flat, categories = categories)
        }
    }

    fun search(query: SearchDto): List<FlatCategoryDto> = transaction {
        val conditions = mutableListOf<Op<Boolean>>()
        query.query?.takeIf { it.isNotBlank() }?.let { q ->
            val pattern = "%${q.trim()}%".lowercase()
            conditions += (Flats.name.lowerCase() like pattern) or
                    (Flats.description.lowerCase() like pattern) or
                    (Flats.address.lowerCase() like pattern)
        }
        query.minPrice?.let { conditions += (Flats.price greaterEq it) }
        query.maxPrice?.let { conditions += (Flats.price lessEq it) }
        query.rooms?.let {
            conditions += if (it == 3) Flats.numberOfRooms greaterEq it else Flats.numberOfRooms eq it
        }
        query.bathrooms?.let {
            conditions += if (it == 3) Flats.numberOfBathrooms greaterEq it else Flats.numberOfBathrooms eq it
        }
        query.isDecorated?.let { conditions += (Flats.hasDecoration eq it) }
        query.homeId?.let { conditions += (Flats.homeId eq it) }
        query.hasParks?.takeIf { it }?.let { conditions += (Flats.features like "%park%") }
        query.hasSchools?.takeIf { it }?.let { conditions += (Flats.features like "%school%") }
        query.hasStores?.takeIf { it }?.let { conditions += (Flats.features like "%store%") }

        val joinQuery = Flats
            .leftJoin(FlatCategories, { Flats.id }, { FlatCategories.flat })
            .leftJoin(Categories, { FlatCategories.categories }, { Categories.id })
        val base = joinQuery.select {
            val baseCondition = if (conditions.isNotEmpty()) conditions.reduce { acc, op -> acc and op } else Op.TRUE
            if (!query.categoriesId.isNullOrEmpty()) {
                baseCondition and (FlatCategories.categories inList query.categoriesId)
            } else {
                baseCondition
            }
        }
        val sorted = when (query.sortBy) {
            "price" -> base.orderBy(Flats.price to SortOrder.valueOf(query.sortOrder?.uppercase() ?: "ASC"))
            "rooms" -> base.orderBy(Flats.numberOfRooms to SortOrder.valueOf(query.sortOrder?.uppercase() ?: "ASC"))
            "area" -> base.orderBy(Flats.area to SortOrder.valueOf(query.sortOrder?.uppercase() ?: "ASC"))
            "location" -> base.orderBy(Flats.address to SortOrder.valueOf(query.sortOrder?.uppercase() ?: "ASC"))
            else -> base
        }
        sorted.groupBy(
            { it.toFlatDto() },
            { row -> row.getOrNull(Categories.id)?.let { row.toCategoryDto() } }
        ).map { (flat, categories) ->
            FlatCategoryDto(flat = flat, categories = categories.filterNotNull())
        }
    }

    private fun ResultRow.toFlatDto() = FlatDto(
        id = this[Flats.id].value,
        name = this[Flats.name],
        description = this[Flats.description],
        images = StringConvertor.parseString(this[Flats.images]),
        layout = this[Flats.layout],
        address = this[Flats.address],
        price = this[Flats.price],
        latitude = this[Flats.latitude]?.toDouble(),
        longitude = this[Flats.longitude]?.toDouble(),
        features = StringConvertor.parseString(this[Flats.features]),
        numberOfRooms = this[Flats.numberOfRooms],
        area = this[Flats.area]?.toDouble(),
        about = this[Flats.about],
        floor = this[Flats.floor],
        homeId = this[Flats.homeId],
        numberOfBathrooms = this[Flats.numberOfBathrooms],
        hasDecoration = this[Flats.hasDecoration],
        numberForSale = this[Flats.numberForSale],
        published = this[Flats.published]
    )

    private fun ResultRow.toCategoryDto() = CategoryDto(
        id = this[Categories.id].value,
        name = this[Categories.name],
        isOnMainPage = this[Categories.isOnMainPage]
    )
}
