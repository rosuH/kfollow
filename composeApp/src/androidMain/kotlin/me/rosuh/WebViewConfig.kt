package me.rosuh

import android.webkit.CookieManager
import android.webkit.WebView

fun WebView.applyConfig() {
    settings.apply {
        domStorageEnabled = true
        javaScriptEnabled = true
    }
    val cookieManager = CookieManager.getInstance()
    cookieManager.setAcceptCookie(true) // General cookie acceptance
    cookieManager.setAcceptThirdPartyCookies(this, true)
}