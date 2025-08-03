package com.kvartalica.utils

import com.kvartalica.config.Config
import com.kvartalica.repository.UserRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object TelegramNotificationService {
    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
            })
        }
    }

    private val baseUrl = Config.botBaseUrl + Config.botToken

    suspend fun notifyAllUsers(message: String) {
        val users = UserRepository.getAllTelegramUsers()
        users.forEach { user ->
            user.let { telegramId ->
                try {
                    sendMessage(telegramId, message)
                } catch (e: Exception) {
                    println("Error $telegramId: ${e.message}")
                }
            }
        }
    }

    private suspend fun sendMessage(chatId: String, text: String) {
        httpClient.post("$baseUrl/sendMessage") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "chat_id" to chatId,
                    "text" to text,
                    "parse_mode" to "HTML"
                )
            )
        }
    }
}