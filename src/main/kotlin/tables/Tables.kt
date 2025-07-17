package kvartalica

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import kotlinx.serialization.json.JsonObject
import java.time.LocalDateTime


// Users table with UUID primary key
object Users : UUIDTable("users") {
    val username = varchar("username", 50)
    val userSurname = varchar("user_surname", 50)
    val userPatronymic = varchar("user_patronymic", 50).nullable()
    val userPhone = varchar("user_phone", 20).nullable().uniqueIndex()
    val userEmail = varchar("user_email", 100).uniqueIndex()
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}



// Categories table
object Categories : IntIdTable("categories") {
    val categoryName = varchar("category_name", 100).uniqueIndex()
}

// Homes table
object Homes : IntIdTable("homes") {
    val homeAddress = varchar("home_address", 255).nullable()
    val yearBuilt = integer("year_built").nullable()
    val totalFloors = integer("total_floors").nullable()
    val ownerId = integer("owner_id").nullable()
    val categoryId = reference("category_id", Categories).nullable()
}

// Flats table
object Flats : IntIdTable("flats") {
    val flatName = varchar("flat_name", 100).nullable()
    val flatValue = decimal("flat_value", 12, 2).nullable()
    val amountOfRooms = integer("amount_of_rooms").nullable().check { it greater 0 }
    val flatFloor = integer("flat_floor").nullable()
    val placementOfFlat = varchar("placement_of_flat", 100).nullable()
    val amountOfBathrooms = integer("amount_of_bathrooms").default(1)
    val isDecorated = bool("is_decorated").default(false)
    val homeId = reference("home_id", Homes)
}

// Descriptions table
object Descriptions : IntIdTable("descriptions") {
    val descriptionText = text("description_text").nullable()
}

// Photos table
object Photos : IntIdTable("photos") {
    val photoUrl = varchar("photo_url", 500)
}


val json = Json {ignoreUnknownKeys = true}

// Footer table
object Footer : IntIdTable("footer") {
    val companyName = varchar("company_name", 200)
    val placementOfSelling = varchar("placement_of_selling", 255).nullable()
    val companyPhone = varchar("company_phone", 20).nullable()
    val companyEmail = varchar("company_email", 100).nullable()
    val socialLinks = json<JsonObject>("social_links", json).nullable()
}