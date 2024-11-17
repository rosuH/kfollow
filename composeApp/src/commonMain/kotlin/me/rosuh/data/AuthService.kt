package me.rosuh.data

import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import kotlinx.serialization.Serializable

class AuthService {
    suspend fun getSession(authToken: String): SessionResponse {
        NetworkManager.setSessionToken(authToken)
        return NetworkManager.request<SessionResponse, Auth.Session>(
            resource = Auth.Session(),
            method = HttpMethod.Get,
            contentType = ContentType.Text.Plain
        )
    }
}