package me.rosuh

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.rosuh.data.SessionResponse

private val masterKey by lazy {
    MasterKey.Builder(KFollowApp.instance)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
}

private val prefs by lazy {
    EncryptedSharedPreferences.create(
        KFollowApp.instance,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}

actual fun saveSessionToken(token: String) {
    prefs.edit().putString("session_token", token).apply()
}

actual fun getSessionToken(): String? {
    return prefs.getString("session_token", null)
}

actual fun saveSessionData(data: SessionResponse) {
    prefs.edit().putString("session_data", Json.encodeToString(data)).apply()
}

actual fun getSessionData(): SessionResponse? {
    return prefs.getString("session_data", null)?.let {
        Json.decodeFromString(it)
    }
}

actual fun clearData() {
    prefs.edit().clear().apply()
}