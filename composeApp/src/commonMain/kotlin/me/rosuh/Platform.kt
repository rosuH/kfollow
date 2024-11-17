package me.rosuh

import me.rosuh.data.OAuthCallback
import me.rosuh.data.SessionResponse

interface Platform {
    val name: String
}

expect fun saveSessionToken(token: String)
expect fun getSessionToken(): String?
expect fun saveSessionData(data: SessionResponse)
expect fun getSessionData(): SessionResponse?
expect fun clearData()

expect fun startOAuth(
    provider: String, // "github" or "google"
    callback: OAuthCallback
)