package com.example.routes
import com.asyncapi.kotlinasyncapi.context.service.AsyncApiExtension
import com.asyncapi.kotlinasyncapi.ktor.AsyncApiPlugin
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.plugins.swagger.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import java.sql.Connection
import java.sql.DriverManager
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.Serializable
import org.slf4j.event.*
import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(RequestValidation) {
        validate<String> { bodyText ->
            if (!bodyText.startsWith("Hello"))
                ValidationResult.Invalid("Body text should start with 'Hello'")
            else ValidationResult.Valid
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        get("/health") {
            call.respondText("OK - Server is healthy")
        }
        get("/test") {
            call.respondText("Test endpoint working")
        }
    }
}

fun Route.userRoutes() {
    route("/users") {
        get {
            try {
                // val users = userService.getAllUsers()
                // call.respond(ApiResponse.success(users))
                call.respond(HttpStatusCode.OK, "Get all users")
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ApiResponse.error<List<User>>("Failed to get users")
                )
            }
        }

        get("/{id}") {
            try {
                val userId = call.parameters["id"] ?: throw ValidationException("User ID is required")
                // val user = userService.getUserById(userId)
                // call.respond(ApiResponse.success(user))
                call.respond(HttpStatusCode.OK, "Get user by ID: $userId")
            } catch (e: NotFoundException) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<User>("User not found")
                )
            } catch (e: ValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse.error<User>(e.message ?: "Validation error")
                )
            }
        }

        post {
            try {
                val request = call.receive<CreateUserRequest>()

                // Validation
                if (!request.userEmail.isValidEmail()) {
                    throw ValidationException("Invalid email format")
                }

                // val user = userService.createUser(request)
                // call.respond(HttpStatusCode.Created, ApiResponse.success(user, "User created successfully"))
                call.respond(HttpStatusCode.Created, "Create user")
            } catch (e: ValidationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse.error<User>(e.message ?: "Validation error")
                )
            } catch (e: ConflictException) {
                call.respond(
                    HttpStatusCode.Conflict,
                    ApiResponse.error<User>("User with this email already exists")
                )
            }
        }

        put("/{id}") {
            try {
                val userId = call.parameters["id"] ?: throw ValidationException("User ID is required")
                val request = call.receive<UpdateUserRequest>()

                // val user = userService.updateUser(userId, request)
                // call.respond(ApiResponse.success(user, "User updated successfully"))
                call.respond(HttpStatusCode.OK, "Update user: $userId")
            } catch (e: NotFoundException) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<User>("User not found")
                )
            }
        }

        delete("/{id}") {
            try {
                val userId = call.parameters["id"] ?: throw ValidationException("User ID is required")
                // userService.deleteUser(userId)
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse.success("User deleted successfully", "User deleted successfully")
                )
            } catch (e: NotFoundException) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse.error<String>("User not found")
                )
            }
        }
    }
}