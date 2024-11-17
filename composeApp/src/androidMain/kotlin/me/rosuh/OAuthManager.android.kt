package me.rosuh

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError

actual fun startOAuth(provider: String, callback: OAuthCallback) {
    val url = Uri.parse("https://app.follow.is/login?provider=$provider")

    // 注册 scheme 处理器
    OAuthActivity.callback = callback

    // 使用 Chrome Custom Tabs 打开登录页
    CustomTabsIntent.Builder()
        .build()
        .launchUrl(KFollowApp.instance, url)
}

// OAuthActivity.kt
class OAuthActivity : AppCompatActivity() {
    companion object {
        var callback: OAuthCallback? = null
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        intent?.data?.let { uri ->
            if (uri.scheme == "follow") {
                uri.getQueryParameter("token")?.let { token ->
                    callback?.onSuccess(token)
                } ?: callback?.onError(OAuthError.NoToken, "Invalid token")
            }
        }
        finish()
    }
}