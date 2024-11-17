package me.rosuh.data

sealed class OAuthError {
    data object InvalidURL : OAuthError()
    data object UserCancel : OAuthError()
    data object Unknown : OAuthError()
    data object NoToken : OAuthError()
}