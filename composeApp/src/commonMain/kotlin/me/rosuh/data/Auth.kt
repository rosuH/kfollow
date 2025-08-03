package me.rosuh.data

import io.ktor.resources.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName


@Resource("/better-auth")
class BetterAuth {
    @Resource("one-time-token/apply")
    class OneTimeTokenApply(val parent: BetterAuth = BetterAuth())
    
    @Resource("get-session")
    class GetSession(val parent: BetterAuth = BetterAuth())
}

@Serializable
data class OneTimeTokenRequest(
    val token: String
)

@Serializable
data class OneTimeTokenResponse(
    @SerialName("user")
    val user: SessionResponse.User
)

