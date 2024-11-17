package me.rosuh

import io.github.aakira.napier.Napier

object FLog {
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        Napier.v(tag, throwable, message)
    }
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        Napier.d(tag, throwable, message)
    }
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        Napier.i(tag, throwable, message)
    }
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Napier.w(tag, throwable, message)
    }
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Napier.e(tag, throwable, message)
    }
    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        Napier.wtf(tag, throwable, message)
    }
}