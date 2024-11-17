package me.rosuh.data

interface OAuthCallback {
    fun onSuccess(token: String)
    fun onError(errorType: OAuthError, message: String)
    fun onCancel()
}
