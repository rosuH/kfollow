package me.rosuh

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import me.rosuh.di.appModule
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kfollow",
    ) {
        KoinApplication(koinConfiguration) {
            App()
        }
    }
}

val koinConfiguration: KoinAppDeclaration = {
    modules(appModule())
}