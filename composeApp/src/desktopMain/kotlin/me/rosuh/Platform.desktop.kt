package me.rosuh

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError
import me.rosuh.data.SessionResponse
import java.awt.Desktop
import java.net.ServerSocket
import java.net.URI
import java.util.prefs.Preferences

private val prefs by lazy {
    Preferences.userRoot().node("auth")
}

actual fun saveSessionToken(token: String) {
    prefs.put("session_token", token)
}

actual fun getSessionToken(): String? {
    return prefs.get("session_token", null)
}

actual fun saveSessionData(data: SessionResponse) {
    prefs.put("session_data", Json.encodeToString(data))
}

actual fun getSessionData(): SessionResponse? {
    return prefs.get("session_data", null)?.let {
        Json.decodeFromString(it)
    }
}

actual fun clearData() {
    prefs.remove("session_token")
    prefs.remove("session_data")
}

actual fun startOAuth(provider: String, callback: OAuthCallback) {
    // 启动本地服务器监听回调
    val server = ServerSocket(0) // 随机可用端口
    val port = server.localPort

    // 启动浏览器
    val url = "https://app.follow.is/login?provider=$provider&redirect_uri=http://localhost:$port"
    Desktop.getDesktop().browse(URI(url))

    // 在协程中处理回调
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val socket = server.accept()
            val reader = socket.getInputStream().bufferedReader()
            val line = reader.readLine()

            if (line.contains("token=")) {
                val token = line.substringAfter("token=").substringBefore(" ")
                callback.onSuccess(token)
            } else {
                callback.onError(OAuthError.NoToken,"No token received")
            }

            // 返回成功页面
            val response = """
                    HTTP/1.1 200 OK
                    Content-Type: text/html
                    
                    <html><body><h1>Authentication successful!</h1><script>window.close()</script></body></html>
                """.trimIndent()

            socket.getOutputStream().write(response.toByteArray())
            socket.close()
            server.close()

        } catch (e: Exception) {
            callback.onError(OAuthError.Unknown, e.message ?: "Unknown error")
            server.close()
        }
    }
}