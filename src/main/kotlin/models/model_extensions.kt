package com.example.models

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

// Extension functions for conversions
fun String.toBigDecimalOrNull(): BigDecimal? {
    return try {
        BigDecimal(this)
    } catch (e: NumberFormatException) {
        null
    }
}

fun BigDecimal?.toStringOrNull(): String? {
    return this?.toString()
}

fun String.toUUIDOrNull(): UUID? {
    return try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

fun UUID.toStringFormatted(): String {
    return this.toString()
}

fun String.toLocalDateTimeOrNull(): LocalDateTime? {
    return try {
        LocalDateTime.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun LocalDateTime.toStringFormatted(): String {
    return this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

// Validation extensions
fun String.isValidEmail(): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return this.matches(emailRegex)
}

fun String.isValidPhone(): Boolean {
    val phoneRegex = "^[+]?[0-9\\s\\-\\(\\)]{7,15}$".toRegex()
    return this.matches(phoneRegex)
}