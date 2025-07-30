package com.kvartalica.repository

import com.kvartalica.dto.PageInfoDto
import com.kvartalica.dto.SocialMediaDto
import com.kvartalica.models.PageInfo
import com.kvartalica.models.SocialMedia
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object PageRepository {
    fun getPageInfo(): PageInfoDto {
        val pageInfo = transaction {
            PageInfo
                .selectAll()
                .limit(1)
                .map { row ->
                    PageInfoDto(
                        phone = row[PageInfo.phone],
                        email = row[PageInfo.email],
                        footerDescription = row[PageInfo.footerDescription],
                        title = row[PageInfo.title],
                        address = row[PageInfo.address],
                        description = row[PageInfo.description],
                        published = row[PageInfo.published],
                    )
                }.firstOrNull()
        }

        if (pageInfo != null) return pageInfo

        return transaction {
            PageInfo.insert {
                it[phone] = null
                it[email] = null
                it[footerDescription] = null
                it[title] = null
                it[address] = null
                it[description] = null
                it[published] = false
            }
            PageInfo
                .selectAll()
                .limit(1)
                .map { row ->
                    PageInfoDto(
                        phone = row[PageInfo.phone],
                        email = row[PageInfo.email],
                        footerDescription = row[PageInfo.footerDescription],
                        title = row[PageInfo.title],
                        address = row[PageInfo.address],
                        description = row[PageInfo.description],
                        published = row[PageInfo.published],
                    )
                }.first()
        }
    }

    fun getSocialMedia(): List<SocialMediaDto> = transaction {
        SocialMedia
            .selectAll()
            .map { socialMedia ->
                SocialMediaDto(
                    id = socialMedia[SocialMedia.id].value,
                    image = socialMedia[SocialMedia.image],
                    link = socialMedia[SocialMedia.link],
                )
            }
    }

    fun updatePageInfo(pageInfoDto: PageInfoDto) {
        transaction {
            val pageInfoId = PageInfo.selectAll().limit(1).firstOrNull()?.get(PageInfo.id)
            if (pageInfoId != null) {
                PageInfo.update({ PageInfo.id eq pageInfoId }) {
                    it[phone] = pageInfoDto.phone
                    it[email] = pageInfoDto.email
                    it[footerDescription] = pageInfoDto.footerDescription
                    it[title] = pageInfoDto.title
                    it[address] = pageInfoDto.address
                    it[description] = pageInfoDto.description
                    it[published] = pageInfoDto.published
                }
            }
        }
    }

    fun createSocialMedia(socialMediaDto: SocialMediaDto) {
        transaction {
            SocialMedia.insert {
                it[image] = socialMediaDto.image
                it[link] = socialMediaDto.link
            }
        }
    }

    fun updateSocialMedia(socialMediaDto: SocialMediaDto) {
        transaction {
            val updatedRows = SocialMedia.update({ SocialMedia.id eq socialMediaDto.id }) {
                it[image] = socialMediaDto.image
                it[link] = socialMediaDto.link
            }
        }
    }

    fun deleteSocialMedia(id: Int) {
        transaction {
            SocialMedia.deleteWhere { SocialMedia.id eq id }
        }
    }
}