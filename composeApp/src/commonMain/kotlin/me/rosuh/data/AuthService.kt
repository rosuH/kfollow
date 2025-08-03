package me.rosuh.data

import io.ktor.client.plugins.cookies.cookies
import io.ktor.http.HttpMethod
import io.ktor.http.decodeURLPart
import kotlinx.serialization.encodeToString
import me.rosuh.FLog

class AuthService {

    suspend fun getAuthCookieToken(oauthToken: String): String {
        FLog.d("AuthService", "Starting getAuthCookieToken with token: ${oauthToken.take(10)}...")

        // Apply one-time token to set auth cookie
        val cookieToken = applyOneTimeToken(oauthToken).decodeURLPart()

        // Set the manual session token for future requests
        NetworkManager.cookieAuthToken = cookieToken.decodeURLPart()
        FLog.d("AuthService", "Set manual session token: ${cookieToken.take(20)}...")

        return cookieToken
    }

    /**
     * Authentication
     * Fetches the session data for the current user.
     */
    suspend fun getSession(): SessionResponse {
        FLog.d("AuthService", "Calling get-session API...")
        val sessionResponse = NetworkManager.request<SessionResponse, BetterAuth.GetSession>(
            BetterAuth.GetSession(),
            method = HttpMethod.Get
        )
        FLog.d("AuthService", "Got session data for user: ${sessionResponse.user.email}")
        return sessionResponse
    }
    
    /**
     * Apply one-time token and extract session token from the response cookie
     */
    private suspend fun applyOneTimeToken(token: String): String {
        FLog.d("AuthService", "Applying one-time token...")
        
        // Log cookies before request
        val cookiesBefore = NetworkManager.client.cookies(NetworkManager.BASE_URL)
        FLog.d("AuthService", "Cookies before one-time token request: ${cookiesBefore}")
        
        // Use direct client call for POST request
        val response = NetworkManager.request<OneTimeTokenResponse, BetterAuth.OneTimeTokenApply>(
            BetterAuth.OneTimeTokenApply(),
            method = HttpMethod.Post,
            body = NetworkManager.json.encodeToString(
            OneTimeTokenRequest(token))
        )
        
        FLog.d("AuthService", "One-time token response status: ${response.user}")
        
        // Log cookies after request
        val cookiesAfter = NetworkManager.client.cookies(NetworkManager.BASE_URL)
        FLog.d("AuthService", "Cookies after one-time token request: $cookiesAfter")
        
        // Extract the session token from the cookie set by the backend
        val authCookie = cookiesAfter.find { it.name == "__Secure-better-auth.session_token" }
        if (authCookie != null) {
            FLog.d("AuthService", "Found auth cookie with raw value: ${authCookie.value}")
            
            // Return the token as-is since URL encoding is not the issue
            // The cookie value is already properly formatted for the server
            FLog.d("AuthService", "Using session token as-is: ${authCookie.value}")
            
            return authCookie.value
        } else {
            throw Exception("No __Secure-better-auth.session_token cookie found in response")
        }
    }
}