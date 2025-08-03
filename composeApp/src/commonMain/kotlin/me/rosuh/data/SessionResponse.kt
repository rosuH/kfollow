package me.rosuh.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class SessionResponse(
    @SerialName("user")
    val user: User,
    @SerialName("session")
    val session: Session,
    @SerialName("role")
    val role: String?
) {
    @OptIn(ExperimentalTime::class)
    fun isExpired(): Boolean {
        return kotlin.time.Clock.System.now() > kotlin.time.Instant.parse(session.expiresAt)
    }

    @Serializable
    data class User(
        @SerialName("name")
        val name: String,
        @SerialName("email")
        val email: String,
        @SerialName("emailVerified")
        val emailVerified: Boolean,
        @SerialName("image")
        val image: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String,
        @SerialName("stripeCustomerId")
        val stripeCustomerId: String?,
        @SerialName("twoFactorEnabled")
        val twoFactorEnabled: Boolean?,
        @SerialName("handle")
        val handle: String,
        @SerialName("socialLinks")
        val socialLinks: Map<String, String> = emptyMap(),
        @SerialName("bio")
        val bio: String?,
        @SerialName("website")
        val website: String?,
        @SerialName("deleted")
        val deleted: String?,
        @SerialName("role")
        val role: String?,
        @SerialName("roleEndAt")
        val roleEndAt: String?,
        @SerialName("id")
        val id: String
    )

    @Serializable
    data class Session(
        @SerialName("expiresAt")
        val expiresAt: String,
        @SerialName("token")
        val token: String,
        @SerialName("createdAt")
        val createdAt: String,
        @SerialName("updatedAt")
        val updatedAt: String,
        @SerialName("ipAddress")
        val ipAddress: String,
        @SerialName("userAgent")
        val userAgent: String,
        @SerialName("userId")
        val userId: String,
        @SerialName("id")
        val id: String
    )
}