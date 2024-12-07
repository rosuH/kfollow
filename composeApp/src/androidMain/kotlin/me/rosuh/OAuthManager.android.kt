package me.rosuh

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import me.rosuh.data.OAuthCallback
import me.rosuh.data.OAuthError

actual fun startOAuth(provider: String, callback: OAuthCallback) {
    // 保存回调以供后续使用
    MainActivity.callback = callback

    // 使用 Chrome Custom Tabs 打开登录页
    MainActivity.mainActivity?.let { activity ->
        // start OAuthActivity
        OAuthActivity.callback = callback
        activity.startActivity(Intent(activity, OAuthActivity::class.java).apply {
            putExtra("provider", provider)
        })
    } ?: run {
        callback.onError(OAuthError.Unknown, "Activity not found")
    }
}