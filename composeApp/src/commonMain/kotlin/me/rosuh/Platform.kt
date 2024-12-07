package me.rosuh

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
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

/**
 * write a open web page function
 * 1. open a web page with a url, and a callback for all state
 * 2. the webview should be inside the app, in Android using Chrome Custom Tabs, in iOS using SFSafariViewController, in Desktop using a browser window
 * 3. the callback should be called when the page is started, when the page is finished, and when the page is closed
 */
sealed class WebPageState {
    abstract val url: String

    data class Started(override val url: String, val extra: Map<String, String>) : WebPageState()
    data class Finished(override val url: String, val code: String, val msg: String) :
        WebPageState()
}
expect fun openWebPage(url: String, callback: (WebPageState) -> Unit)

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass