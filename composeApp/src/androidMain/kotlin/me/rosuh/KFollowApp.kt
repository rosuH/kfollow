package me.rosuh

import android.app.Application
import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KFollowApp : Application() {
    companion object {
        lateinit var instance: KFollowApp
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KFollowApp)
        }
        Napier.base(DebugAntilog())
    }
}