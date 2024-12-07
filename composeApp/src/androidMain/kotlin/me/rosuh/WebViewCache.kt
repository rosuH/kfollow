package me.rosuh

import android.content.Context
import android.content.MutableContextWrapper
import android.webkit.WebView
import androidx.collection.LruCache

object WebViewCache {
    const val TAG = "WebViewCache"

    const val CommonTag = "CommonTag"

    private val contextWrapper by lazy {
        MutableContextWrapper(KFollowApp.instance)
    }

    private val pool by lazy {
        LruCache<String, WebView>(3)
    }

    fun getWebView(tag: String = CommonTag, context: Context = KFollowApp.instance): WebView {
        return pool[tag] ?: createWebView(tag, context)
    }

    fun prepareCommonWebView(context: Context = KFollowApp.instance) {
        getWebView(CommonTag, context)
    }

    private fun createWebView(tag: String, context: Context): WebView {
        val webView = WebView(contextWrapper)
        pool.put(tag, webView)
        contextWrapper.baseContext = context
        return webView
    }

    fun clear() {
        pool.evictAll()
    }
}