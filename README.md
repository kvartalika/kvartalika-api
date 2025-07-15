# KvartalicaBackend

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                                   | Description                                                                        |
|------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| [AsyncAPI](https://start.ktor.io/p/asyncapi)                           | Generates and serves AsyncAPI documentation                                        |
| [CORS](https://start.ktor.io/p/cors)                                   | Enables Cross-Origin Resource Sharing (CORS)                                       |
| [Compression](https://start.ktor.io/p/compression)                     | Compresses responses using encoding algorithms like GZIP                           |
| [Default Headers](https://start.ktor.io/p/default-headers)             | Adds a default set of headers to HTTP responses                                    |
| [HttpsRedirect](https://start.ktor.io/p/https-redirect)                | Redirects insecure HTTP requests to the respective HTTPS endpoint                  |
| [Routing](https://start.ktor.io/p/routing)                             | Provides a structured routing DSL                                                  |
| [OpenAPI](https://start.ktor.io/p/openapi)                             | Serves OpenAPI documentation                                                       |
| [Simple Cache](https://start.ktor.io/p/simple-cache)                   | Provides API for cache management                                                  |
| [Simple Memory Cache](https://start.ktor.io/p/simple-memory-cache)     | Provides memory cache for Simple Cache plugin                                      |
| [Swagger](https://start.ktor.io/p/swagger)                             | Serves Swagger UI for your project                                                 |
| [Authentication](https://start.ktor.io/p/auth)                         | Provides extension point for handling the Authorization header                     |
| [Authentication Basic](https://start.ktor.io/p/auth-basic)             | Handles 'Basic' username / password authentication scheme                          |
| [Authentication JWT](https://start.ktor.io/p/auth-jwt)                 | Handles JSON Web Token (JWT) bearer authentication scheme                          |
| [CSRF](https://start.ktor.io/p/csrf)                                   | Cross-site request forgery mitigation                                              |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)     | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization) | Handles JSON serialization using kotlinx.serialization library                     |
| [Sessions](https://start.ktor.io/p/ktor-sessions)                      | Adds support for persistent sessions through cookies or headers                    |
| [AutoHeadResponse](https://start.ktor.io/p/auto-head-response)         | Provides automatic responses for HEAD requests                                     |
| [Request Validation](https://start.ktor.io/p/request-validation)       | Adds validation for incoming requests                                              |
| [Static Content](https://start.ktor.io/p/static-content)               | Serves static files from defined locations                                         |
| [Call Logging](https://start.ktor.io/p/call-logging)                   | Logs client requests                                                               |
| [Jackson](https://start.ktor.io/p/ktor-jackson)                        | Handles JSON serialization using Jackson library                                   |
| [Postgres](https://start.ktor.io/p/postgres)                           | Adds Postgres database to your application                                         |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

