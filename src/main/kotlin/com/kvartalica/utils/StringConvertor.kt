package com.kvartalica.utils

object StringConvertor {
    private const val SEPARATOR = "#"

    fun joinToString(l: List<String>?): String {
        return l?.joinToString(SEPARATOR) { it } ?: ""
    }

    fun parseString(s: String?): List<String> {
        if (s.isNullOrBlank()) {
            return emptyList()
        }
        return s.split(SEPARATOR)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}