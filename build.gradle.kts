val h2_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val postgres_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.10"

}

group = "com.kvartalica"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
}

dependencies {
    // Core
    implementation("io.ktor:ktor-server-core:3.2.1")
    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.1")
    implementation("io.ktor:ktor-serialization-jackson:3.2.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.2.1")
    // HTTP Client
    implementation("io.ktor:ktor-client-core:3.2.1")
    implementation("io.ktor:ktor-client-apache:3.2.1")
    implementation("io.ktor:ktor-client-cio:3.2.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.1")
    // Authentication & Security
    implementation("io.ktor:ktor-server-auth:3.2.1")
    implementation("io.ktor:ktor-server-auth-jwt:3.2.1")
    implementation("io.ktor:ktor-server-csrf:3.2.1")
    // Environment
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    // Session Management
    implementation("io.ktor:ktor-server-sessions:3.2.1")
    // CORS & Headers
    implementation("io.ktor:ktor-server-cors:3.2.1")
    implementation("io.ktor:ktor-server-default-headers:3.2.1")
    implementation("io.ktor:ktor-server-auto-head-response:3.2.1")
    implementation("io.ktor:ktor-server-conditional-headers:3.2.1")
    implementation("io.ktor:ktor-server-compression:3.2.1")
    implementation("io.ktor:ktor-server-forwarded-header:3.2.1")
    // OpenAPI & Swagger
    implementation("io.ktor:ktor-server-openapi:3.2.1")
    implementation("io.ktor:ktor-server-swagger:3.2.1")
    // Logging & Validation
    implementation("io.ktor:ktor-server-call-logging:3.2.1")
    implementation("io.ktor:ktor-server-request-validation:3.2.1")
    implementation("io.ktor:ktor-server-status-pages:3.2.1")
    // Netty
    implementation("io.ktor:ktor-server-netty:3.2.1")
    // Database Drivers
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:$h2_version")
    // Connection Pool
    implementation("com.zaxxer:HikariCP:5.0.1")
    // Exposed ORM
    implementation("org.jetbrains.exposed:exposed-core:0.52.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.52.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.52.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.52.0")
    implementation("org.jetbrains.exposed:exposed-json:0.52.0")
    // AsyncAPI Generator
    implementation("org.openfolder:kotlin-asyncapi-ktor:3.1.1")
    // Security Helpers
    implementation("org.mindrot:jbcrypt:0.4")
    // Testing
    testImplementation("io.ktor:ktor-server-test-host:3.2.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
