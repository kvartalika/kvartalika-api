FROM gradle:8-jdk17 AS builder

WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradlew ./
COPY gradle ./gradle
RUN ./gradlew --no-daemon dependencyInsight --configuration runtimeClasspath
COPY src ./src
RUN ./gradlew --no-daemon clean build

FROM eclipse-temurin:17-jre-jammy
WORKDIR /home/appuser
COPY --from=builder /app/build/libs/*.jar ./app.jar
EXPOSE 8080
# ENV PORT=8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
