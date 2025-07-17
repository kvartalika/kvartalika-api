package com.example.models

class ValidationException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)
class DatabaseException(message: String, cause: Throwable? = null) : Exception(message, cause)