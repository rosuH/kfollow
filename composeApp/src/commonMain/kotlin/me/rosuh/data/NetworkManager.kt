package me.rosuh.data
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.rosuh.FLog

object NetworkManager {
    const val BASE_URL = "https://api.follow.is"
    
    var cookieAuthToken: String? = null

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
            // Add standard browser headers for better compatibility
            header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36")
            header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            header("DNT", "1")
            header("Sec-Fetch-Site", "cross-site")
            header("Sec-Fetch-Mode", "cors")
            header("Sec-Fetch-Dest", "empty")
            header("Cache-Control", "no-cache")
            header("Pragma", "no-cache")

            // Add headers for better-auth compatibility
            header("x-app-name", "Folo Web")
            header("x-app-platform", "desktop/macos/dmg")
            header("x-app-version", "0.6.2")
            header("Origin", "app://folo.is")
            header("Referer", "https://app.folo.is")

            // Set Cookie header directly if token is available
            cookieAuthToken?.let { token ->
                // 💩workaround for Ktor bug with cookies
                header("Cookie", "__Secure-better-auth.session_token=$token")
//                cookie("__Secure-better-auth.session_token", token, path = "/")
            }
        }
    }

    /**
     * Main request method for better-auth that uses automatic cookie management
     */
    suspend inline fun <reified T, reified R : Any> request(
        resource: R,
        method: HttpMethod = HttpMethod.Get,
        body: String = "",
        contentType: ContentType = ContentType.Application.Json
    ): T {
        // Debug: Log current cookies before making request
        val currentCookies = client.cookies(BASE_URL)
        FLog.d("NetworkManager", "Current cookies for $BASE_URL: ${currentCookies.joinToString(", ") { "${it.name}=${it.value}" }}")
        FLog.d("NetworkManager", "Manual session token: ${cookieAuthToken ?: "none"}")
        val result = when (method) {
            HttpMethod.Get -> client.get(resource) {
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,application/json;q=0.8,*/*;q=0.7")
                if (body.isNotEmpty()) {
                    header(HttpHeaders.ContentType, contentType)
                    setBody(body)
                }
//                manualSessionToken?.let { token ->
//                    FLog.d("NetworkManager", "Using manual session token: $token")
//                    cookie("__Secure-better-auth.session_token", token, path = "/")
//                }
            }
            HttpMethod.Post -> client.post(resource) {
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,application/json;q=0.8,*/*;q=0.7")
                if (body.isNotEmpty()) {
                    header(HttpHeaders.ContentType, contentType)
                    setBody(body)
                }
//                manualSessionToken?.let { token ->
//                    cookie("__Secure-better-auth.session_token", token, path = "/")
//                }
                // Authentication is handled by defaultRequest Cookie header
                FLog.d("NetworkManager", "POST: Authentication handled by default Cookie header")
            }
            HttpMethod.Put -> client.put(resource) {
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,application/json;q=0.8,*/*;q=0.7")
                if (body.isNotEmpty()) {
                    header(HttpHeaders.ContentType, contentType)
                    setBody(body)
                }
                // Authentication is handled by defaultRequest Cookie header
                FLog.d("NetworkManager", "PUT: Authentication handled by default Cookie header")
            }
            HttpMethod.Delete -> client.delete(resource) {
                header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,application/json;q=0.8,*/*;q=0.7")
                if (body.isNotEmpty()) {
                    header(HttpHeaders.ContentType, contentType)
                    setBody(body)
                }
                // Authentication is handled by defaultRequest Cookie header
                FLog.d("NetworkManager", "DELETE: Authentication handled by default Cookie header")
            }
            else -> throw UnsupportedOperationException("HTTP method $method is not supported")
        }
        
        // Debug: Log cookies after request
        val newCookies = client.cookies(BASE_URL)
        FLog.d("NetworkManager", "Cookies after request: ${newCookies.joinToString(", ") { "${it.name}=${it.value}" }}")
        
        return result.body()
    }
}