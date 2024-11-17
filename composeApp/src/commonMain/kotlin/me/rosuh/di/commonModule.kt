package me.rosuh.di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import me.rosuh.MainViewModel

val commonModule = module {
    singleOf(::MainViewModel)
}