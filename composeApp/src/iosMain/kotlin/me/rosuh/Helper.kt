package me.rosuh

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.rosuh.di.appModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(appModule())
    }
}

fun initLogger(){
    Napier.base(DebugAntilog())
}