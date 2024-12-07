package me.rosuh

import me.rosuh.di.appModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(appModule())
    }
}

fun initLogger(){
}