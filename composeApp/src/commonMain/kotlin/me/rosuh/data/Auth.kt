package me.rosuh.data

import io.ktor.resources.*
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Resource("/auth")
class Auth {
    @Resource("session")
    class Session(val parent: Auth = Auth())
    
    @Resource("csrf")
    class CSRF(val parent: Auth = Auth())
}

@Serializable
data class CSRFTokenResponse(
    val csrfToken: String
)

