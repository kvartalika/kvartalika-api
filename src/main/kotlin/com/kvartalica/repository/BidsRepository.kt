package com.kvartalica.repository

import com.kvartalica.dto.BidsDto
import com.kvartalica.models.Bids
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object BidsRepository {
    fun create(bid: BidsDto) {
        transaction {
            Bids.insert {
                it[name] = bid.name
                it[surname] = bid.surname
                it[patronymic] = bid.patronymic
                it[email] = bid.email
                it[phone] = bid.phone
                it[createdAt] = bid.createdAt
                it[isChecked] = bid.isChecked
            }
        }
    }

    fun findById(id: Int): BidsDto? = transaction {
        Bids.selectAll()
            .where { Bids.id eq id }
            .limit(1)
            .map {
                BidsDto(
                    id = it[Bids.id].value,
                    name = it[Bids.name],
                    surname = it[Bids.surname],
                    patronymic = it[Bids.patronymic],
                    email = it[Bids.email],
                    phone = it[Bids.phone],
                    createdAt = it[Bids.createdAt],
                    isChecked = it[Bids.isChecked]
                )
            }
            .singleOrNull()
    }

    fun findAll(): List<BidsDto> = transaction {
        Bids.selectAll()
            .orderBy(Bids.createdAt, SortOrder.DESC)
            .map {
                BidsDto(
                    id = it[Bids.id].value,
                    name = it[Bids.name],
                    surname = it[Bids.surname],
                    patronymic = it[Bids.patronymic],
                    email = it[Bids.email],
                    phone = it[Bids.phone],
                    createdAt = it[Bids.createdAt],
                    isChecked = it[Bids.isChecked]
                )
            }
    }

    fun update(id: Int, bid: BidsDto) {
        transaction {
            Bids.update({ Bids.id eq id }) {
                it[name] = bid.name
                it[surname] = bid.surname
                it[patronymic] = bid.patronymic
                it[email] = bid.email
                it[phone] = bid.phone
                it[isChecked] = bid.isChecked
            }
        }
    }

    fun delete(id: Int) {
        transaction {
            Bids.deleteWhere { Bids.id eq id }
        }
    }
}