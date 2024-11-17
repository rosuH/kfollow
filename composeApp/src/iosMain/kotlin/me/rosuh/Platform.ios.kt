package me.rosuh

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCClass
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError
import me.rosuh.data.SessionResponse
import platform.AuthenticationServices.ASWebAuthenticationPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASWebAuthenticationSession
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIWindow
import platform.darwin.NSObject
import platform.darwin.NSUInteger

private val defaults = NSUserDefaults.standardUserDefaults

actual fun saveSessionToken(token: String) {
    defaults.setObject(token, "session_token")
}

actual fun getSessionToken(): String? {
    return defaults.stringForKey("session_token")
}

actual fun saveSessionData(data: SessionResponse) {
    defaults.setObject(Json.encodeToString(data), "session_data")
}

actual fun getSessionData(): SessionResponse? {
    return defaults.stringForKey("session_data")?.let {
        Json.decodeFromString(it)
    }
}

actual fun clearData() {
    defaults.removeObjectForKey("session_token")
    defaults.removeObjectForKey("session_data")
}


private var webAuthSession: ASWebAuthenticationSession? = null

@ExperimentalForeignApi
actual fun startOAuth(provider: String, callback: OAuthCallback) {
    val url = NSURL.URLWithString("https://app.follow.is/login?provider=$provider") ?: run {
        callback.onError(OAuthError.InvalidURL, "Invalid URL")
        return
    }
    webAuthSession = ASWebAuthenticationSession(
        uRL = url,
        callbackURLScheme = "follow"
    ) { callbackURL: NSURL?, error: NSError? ->
        when {
            error != null -> {
                when (error.code) {
                    1L -> callback.onCancel()
                    else -> callback.onError(OAuthError.Unknown, error.localizedDescription)
                }
            }
            callbackURL != null -> {
                val components = NSURLComponents(uRL = callbackURL, resolvingAgainstBaseURL = false)
                val queryItems = components.queryItems as? List<NSURLQueryItem>
                val token = queryItems?.firstOrNull {
                    it.name == "token"
                }?.value

                if (token != null) {
                    callback.onSuccess(token)
                } else {
                    callback.onError(OAuthError.NoToken, "No token received")
                }
            }
            else -> callback.onCancel()
        }
    }

    val contextProvider = object : NSObject(), ASWebAuthenticationPresentationContextProvidingProtocol {
        override fun presentationAnchorForWebAuthenticationSession(
            session: ASWebAuthenticationSession
        ): UIWindow {
            return UIApplication.sharedApplication.keyWindow!!
        }

        override fun description(): String {
            return "ASWebAuthenticationPresentationContextProvider"
        }

        override fun hash(): ULong {
            return hashCode().toULong()
        }

        override fun isEqual(other: Any?): Boolean {
            return this === other
        }
    }

    webAuthSession?.presentationContextProvider = contextProvider
    webAuthSession?.prefersEphemeralWebBrowserSession = true
    webAuthSession?.start()
}