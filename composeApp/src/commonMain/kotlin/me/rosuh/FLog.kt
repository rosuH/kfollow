package me.rosuh

import io.github.aakira.napier.Napier

object FLog {
    fun v(tag: String, message: String, throwable: Throwable? = null) {
        Napier.v(message, throwable, tag)
    }
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        Napier.d(message, throwable, tag)
    }
    fun i(tag: String, message: String, throwable: Throwable? = null) {
        Napier.i(message, throwable, tag)
    }
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Napier.w(message, throwable, tag)
    }
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Napier.e(message, throwable, tag)
    }
    fun wtf(tag: String, message: String, throwable: Throwable? = null) {
        Napier.wtf(message, throwable, tag)
    }
}