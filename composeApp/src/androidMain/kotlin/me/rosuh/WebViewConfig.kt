package me.rosuh

import android.webkit.WebView

fun WebView.applyConfig() {
    settings.apply {
        javaScriptEnabled = true
    }
}