package com.kvartalica.repository

import com.kvartalica.dto.CategoryDto
import com.kvartalica.models.Categories
import com.kvartalica.models.FlatCategories
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object CategoriesRepository {
    fun getCategories(): List<CategoryDto> = transaction {
        Categories.selectAll().map { row ->
            CategoryDto(
                id = row[Categories.id].value,
                name = row[Categories.name],
                isOnMainPage = row[Categories.isOnMainPage]
            )
        }
    }

    fun getCategory(id: Int): CategoryDto? = transaction {
        Categories.select { Categories.id eq id }
            .map { row ->
                CategoryDto(
                    id = row[Categories.id].value,
                    name = row[Categories.name],
                    isOnMainPage = row[Categories.isOnMainPage]
                )
            }
            .singleOrNull()
    }

    fun addCategory(dto: CategoryDto) {
        transaction {
            Categories.insert { itRow ->
                itRow[name] = dto.name
                itRow[isOnMainPage] = dto.isOnMainPage
            }
        }
    }

    fun updateCategory(id: Int, dto: CategoryDto) {
        transaction {
            Categories.update({ Categories.id eq id }) { itRow ->
                itRow[name] = dto.name
                itRow[isOnMainPage] = dto.isOnMainPage
            }
        }
    }

    fun deleteCategory(id: Int) {
        transaction {
            FlatCategories.deleteWhere { FlatCategories.categories eq id }
            Categories.deleteWhere { Categories.id eq id }
        }
    }

    fun addCategoryToFlat(fId: Int, cId: Int) {
        transaction {
            FlatCategories.insert { row ->
                row[FlatCategories.flat] = fId
                row[FlatCategories.categories] = cId
            }
        }
    }

    fun deleteCategoryFromFlat(fId: Int, cId: Int) {
        transaction {
            FlatCategories.deleteWhere {
                (FlatCategories.flat eq fId) and
                        (FlatCategories.categories eq cId)
            }
        }
    }
}