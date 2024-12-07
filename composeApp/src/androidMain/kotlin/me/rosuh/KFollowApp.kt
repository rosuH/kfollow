package me.rosuh

import android.app.Application
import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.rosuh.WebViewCache.prepareCommonWebView
import me.rosuh.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KFollowApp : Application() {
    companion object {
        lateinit var instance: KFollowApp
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
        Napier.base(DebugAntilog())
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KFollowApp)
            modules(appModule())
        }
        prepareCommonWebView()
    }
}