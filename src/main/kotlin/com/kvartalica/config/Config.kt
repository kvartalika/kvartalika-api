package com.kvartalica.config

object Config {
    const val DB_MAX_POOL_SIZE = 10

    val secret: String by lazy {
        System.getenv("SECRET")
    }

    val issuer: String by lazy {
        System.getenv("ISSUER")
    }

    val baseDir: String by lazy {
        System.getenv("BASE_DIR")
    }

    val botToken: String by lazy {
        System.getenv("BOT_TOKEN")
    }

    val botBaseUrl: String by lazy {
        System.getenv("BOT_BASE_URL")
    }

    val initName: String by lazy {
        System.getenv("INIT_NAME")
    }

    val initSurname: String by lazy {
        System.getenv("INIT_SURNAME")
    }

    val initEmail: String by lazy {
        System.getenv("INIT_EMAIL")
    }

    val initPassword: String by lazy {
        System.getenv("INIT_PASSWORD")
    }

    val initTelegramId: String by lazy {
        System.getenv("INIT_TELEGRAM_ID")
    }

    val postgresUser: String by lazy {
        System.getenv("POSTGRES_USER")
    }

    val postgresPassword: String by lazy {
        System.getenv("POSTGRES_PASSWORD")
    }

    val postgresDb: String by lazy {
        System.getenv("POSTGRES_DB")
    }

    val postgresUrl: String by lazy {
        System.getenv("POSTGRES_URL")
    }

    val allowHostHttp: String by lazy {
        System.getenv("ALLOWED_HOST_HTTP")
    }

    val allowHostHttps: String by lazy {
        System.getenv("ALLOWED_HOST_HTTPS")
    }
}
