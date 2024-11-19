package me.rosuh.data
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.cookie
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import me.rosuh.FLog

object NetworkManager {
    const val BASE_URL = "https://api.follow.is"
    internal var sessionToken: String = ""
    internal var csrfToken: String = ""

    val json by lazy {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    val client = HttpClient {
        install(Resources)
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = object: Logger {
                override fun log(message: String) {
                    FLog.v("NetworkManager", message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpCookies)
        install(HttpTimeout) {
            requestTimeoutMillis = 30000
            connectTimeoutMillis = 30000
            socketTimeoutMillis = 30000
        }
        defaultRequest {
            url(BASE_URL)
        }
    }

    fun setSessionToken(token: String) {
        sessionToken = token
    }

    fun setCsrfToken(token: String) {
        csrfToken = token
    }

    fun clearTokens() {
        sessionToken = ""
        csrfToken = ""
    }

    suspend fun getCsrfToken(): String {
        val response = client.get(Auth.CSRF()).body<CSRFTokenResponse>()
        csrfToken = response.csrfToken
        return response.csrfToken
    }

    internal suspend inline fun <reified T, reified R : Any> request(
        resource: R,
        method: HttpMethod = HttpMethod.Get,
        body: String = "",
        contentType: ContentType = ContentType.Application.Json
    ): T {
        val token = sessionToken
        val csrf = getCsrfToken()

        return when (method) {
            HttpMethod.Get -> client.get(resource) {
                configureRequest(token, csrf, body, contentType)
            }
            HttpMethod.Post -> client.post(resource) {
                configureRequest(token, csrf, body, contentType)
            }
            HttpMethod.Put -> client.put(resource) {
                configureRequest(token, csrf, body, contentType)
            }
            HttpMethod.Delete -> client.delete(resource) {
                configureRequest(token, csrf, body, contentType)
            }
            else -> throw UnsupportedOperationException("HTTP method $method is not supported")
        }.body()
    }

    private fun HttpRequestBuilder.configureRequest(
        token: String,
        csrf: String,
        body: String = "",
        contentType: ContentType = ContentType.Application.Json
    ) {
        header("X-CSRF-Token", csrf)
        header(HttpHeaders.Accept, contentType)
        contentType(contentType)
        header("x-app-version", "0.2.1-beta.0")
        cookie("authjs.session-token", token)
        cookie("authjs.csrf-token", csrf)
        setBody(body)
    }
}