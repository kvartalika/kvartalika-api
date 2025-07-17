package com.example.tables

import com.example.models.*
import kvartalica.Categories
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class CategoryDAO {

    fun getAllCategories(): List<Category> = transaction {
        Categories.selectAll().map { toCategory(it) }
    }

    fun getCategoryById(id: Int): Category? = transaction {
        Categories.selectAll().where { Categories.id eq id }
            .mapNotNull { toCategory(it) }
            .singleOrNull()
    }

    fun getCategoryByName(name: String): Category? = transaction {
        Categories.selectAll().where { Categories.categoryName eq name }
            .mapNotNull { toCategory(it) }
            .singleOrNull()
    }

    fun createCategory(request: CreateCategoryRequest): Category = transaction {
        val categoryId = Categories.insertAndGetId {
            it[categoryName] = request.categoryName
        }

        Category(
            categoryId = categoryId.value,
            categoryName = request.categoryName
        )
    }

    fun updateCategory(id: Int, request: UpdateCategoryRequest): Category? = transaction {
        val updateCount = Categories.update({ Categories.id eq id }) {
            it[categoryName] = request.categoryName
        }

        if (updateCount > 0) getCategoryById(id) else null
    }

    fun deleteCategory(id: Int): Boolean = transaction {
        Categories.deleteWhere { Categories.id eq id } > 0
    }

    private fun toCategory(row: ResultRow): Category = Category(
        categoryId = row[Categories.id].value,
        categoryName = row[Categories.categoryName]
    )
}